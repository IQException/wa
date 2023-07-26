package com.ximalaya.wa.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.ximalaya.kafka.producer.KafkaProducerTemplate;
import com.ximalaya.wa.converter.WaConverter;
import com.ximalaya.wa.dao.ManageDao;
import com.ximalaya.wa.manage.model.WaManage;
import com.ximalaya.wa.model.xml.CommonResponse;
import com.ximalaya.wa.model.xml.Data;
import com.ximalaya.wa.model.xml.ResponseInfo;
import com.ximalaya.wa.sender.model.Constant;
import com.ximalaya.wa.sender.model.ModelMapping;
import com.ximalaya.wa.sender.util.FileUtil;
import com.ximalaya.wa.sender.util.SFTPUploadUtil;
import com.ximalaya.wa.service.IWAManageService;
import com.ximalaya.wa.util.DataUtil;
import com.ximalaya.wa.util.OxmUtil;
import com.ximalaya.wa.util.WAXmlParseUtil;
import com.ximalaya.xmsms.inf.api.SendMessageService;

@SuppressWarnings("all")
@Service
public class WAManageService implements IWAManageService {

	private final Logger				logger	= LoggerFactory.getLogger(WAManageService.class);

	@Autowired
	@Qualifier("userTemplate")
	private KafkaProducerTemplate		userTemplate;

	@Autowired
	private ModelMapping				modelMap;

	@Autowired
	private ManageDao					dao;

	@Autowired
	private SendMessageService.Iface	sendMessageService;
	
	private final String[] paramCloseAccount = {"0601_0","0612_0","0627_0","0628_0","0629_0","0630_0","0631_0","0632_0","0652_0","0653_0","0654_0","0655_0","0656_0","0657_0"};

	@Override
	public void manage(String opCode, Map<String, String> criteria, String msgId, String opId) {

		try {
			logger.info("receive wa manage request,opCode:{},criteria:{}", opCode, criteria);

			List<Data> status = DataUtil.getResonseData("0", "0");

			if (criteria.size() < 3) {
				status = DataUtil.getResonseData("1", "11");
				put2XmlAndUpload(status, opCode, msgId, opId);
				logger.info("the wa param was wrong,criteria:{}", criteria);
				return;
			}else{
				put2XmlAndUpload(status, opCode, msgId, opId);
			}

			// 解析criteria，需要手动管控的写入到数据库中，ip也需要手动管控
			WaManage waManage = new WaManage();
			List<String> paramKey = new ArrayList<>();

			if (opCode.equals("FREEZEAREAS")) {

				String startIp = criteria.get("START_IP");
				String endIp = criteria.get("END_IP");
				String longitude = criteria.get("LONGITUDE");
				String latitude = criteria.get("LATITUDE");

				if (longitude != null && latitude != null) {
					waManage.setManageKey("POSITION_INFO");
					waManage.setManageValue("(" + longitude + "," + latitude + ")");
				}
				if (startIp != null || endIp != null) {
					waManage.setManageKey("IP_ZONE");
					if (startIp == null) {
						waManage.setManageValue(endIp);
					} else if (endIp == null) {
						waManage.setManageValue(startIp);
					} else {
						waManage.setManageValue(startIp + "-" + endIp);
					}
				}

			}

			for (Map.Entry<String, String> entry : criteria.entrySet()) {

				String key = entry.getKey();

				if (key.contains("PARAM_NAME")) {
					String param = key.substring(key.lastIndexOf("_") + 1) + "_" + entry.getValue();
					paramKey.add(param);
				} else if (key.equals("BEGINTIME")) {
					waManage.setStartTime(Long.valueOf(entry.getValue()));
				} else if (key.equals("ENDTIME")) {
					waManage.setEndTime(Long.valueOf(entry.getValue()));
				} else {
					if (!opCode.equals("FREEZEAREAS")) { // FREEZEAREAS的manageKey已经处理
						waManage.setManageKey(key);
						waManage.setManageValue(entry.getValue());
					}
				}

			}

			waManage.setParamKey(JSON.toJSONString(paramKey));
			waManage.setData(getData(opCode, criteria, msgId, opId));
			waManage.setCreateAt(new Date());

			// 存表
			dao.insert(waManage);
			
			// 用户支持自动封号
			if (opCode.equals("FREEZEUSERS")) {
				sendManageUserMsg(opCode, criteria, msgId, opId,waManage.getManageKey(),paramKey);
			}
			
			// 发送提示消息给审核
			sendMessageService.sendRemindMsg("15152725771", "喜马拉雅系统说，这个世界上最酷的审核收到了网安的指令！请求马上处理！！！", "1");

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	// FREASEUSER是可以自动管控的，当然也要存入到表中
	private void sendManageUserMsg(String opCode, Map<String, String> criteria, String msgId, String opId,String manageKey,List<String> paramKey) {
		
		// 判断是否自动封号
		if (manageKey.equals("NICKNAME") || manageKey.equals("USERTYPE")) {
			return;
		}
		
		boolean isAuto = false;
		
		List<String> closeAccountParam = Arrays.asList(paramCloseAccount);
		for (String param : paramKey) {
			if (closeAccountParam.contains(param)) {
				isAuto = true;
			}
		}
		
		if (!isAuto) {
			return;
		}

		// 自动封号操作
		boolean isSuccess = false;

		List<Data> status = DataUtil.getResonseData("0", "0");

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put(WAXmlParseUtil.OPCODE, opCode);
		resultMap.put(WAXmlParseUtil.OPID, opId);
		resultMap.put(WAXmlParseUtil.MSGID, msgId);

		try {
			for (Map.Entry<String, String> entry : criteria.entrySet()) {
				if (entry.getKey().equals("USER_ACCOUNT")) {
					String account = entry.getValue();

					boolean is_email = account.contains("@");
					boolean is_mobile = (account.length() == 11);

					if (is_email) {
						resultMap.put("email", account);
					} else if (is_mobile) {
						resultMap.put("mobile", account);
					} else {
						resultMap.put("uid", Long.valueOf(account));
					}
				} else if (entry.getKey().equals("USER_INTENRALID")) {
					resultMap.put("uid", Long.valueOf(entry.getValue()));
				} else {
					resultMap.put(entry.getKey(), entry.getValue());
				}
			} // parse account and criteria

			isSuccess = true;

		} catch (Exception e) {
			logger.error("FREEZEUSERS param error,criteria : {}", criteria);
			status = DataUtil.getResonseData("1", "11");
		}

		if (isSuccess) {
			try {
				userTemplate.sendSync(JSON.toJSONString(resultMap));
				logger.info("send msg : {}", resultMap);
			} catch (Exception e) {
				logger.info("send msg failure : {}");
				status = DataUtil.getResonseData("1", "99");
			} // send msg
		}

		put2XmlAndUpload(status, opCode, msgId, opId); // write and upload

	}

	private void put2XmlAndUpload(List<Data> status, String opCode, String msgId, String opId) {
		try {
			ResponseInfo<CommonResponse> result = WaConverter.convertToCommonResponse(status, null, opCode, msgId,
					opId);
			String dir = FileUtil.getManageXMLDir(opCode, Constant.WA_RESPONSE, msgId);
			OxmUtil.toXML(result.getResponse(), new File(dir)); // write to xml

			SFTPUploadUtil.uploadFile(SFTPUploadUtil.JINGKOU, dir); // upload
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	// 将要生成的xml的信息存入到数据库中
	private String getData(String opCode, Map<String, String> criteria, String msgId, String opId) {

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put(WAXmlParseUtil.OPCODE, opCode);
		resultMap.put(WAXmlParseUtil.OPID, opId);
		resultMap.put(WAXmlParseUtil.MSGID, msgId);

		for (Map.Entry<String, String> entry : criteria.entrySet()) {
			resultMap.put(entry.getKey(), entry.getValue());
		}

		return JSON.toJSONString(resultMap);

	}

}
