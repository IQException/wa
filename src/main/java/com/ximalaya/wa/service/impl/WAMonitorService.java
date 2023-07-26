package com.ximalaya.wa.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.thrift.TException;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ximalaya.passport.thrift.Passport;
import com.ximalaya.passport.thrift.RemotePassportService;
import com.ximalaya.wa.converter.WaConverter;
import com.ximalaya.wa.dao.MonitorDao;
import com.ximalaya.wa.model.xml.Data;
import com.ximalaya.wa.model.xml.ResponseInfo;
import com.ximalaya.wa.monitor.model.Condition;
import com.ximalaya.wa.sender.model.Constant;
import com.ximalaya.wa.sender.model.ModelMapping;
import com.ximalaya.wa.sender.util.FileUtil;
import com.ximalaya.wa.sender.util.SFTPUploadUtil;
import com.ximalaya.wa.service.IWAMonitorService;
import com.ximalaya.wa.util.DataUtil;
import com.ximalaya.wa.util.ElasticSearchUtil;
import com.ximalaya.wa.util.OxmUtil;

@SuppressWarnings("all")
@Service
public class WAMonitorService implements IWAMonitorService {

	private final Logger				logger	= LoggerFactory.getLogger(WAMonitorService.class);

	@Autowired
	private MonitorDao					dao;

	@Autowired
	private ModelMapping				modelMap;

	@Autowired
	private RemotePassportService.Iface	passportService;

	@Value("${monitor.dir}")
	private String						MONITOR_DIR;

	@Override
	public void monitor(String opCode, Map<String, String> criteria, String msgId, String opId) {

		logger.info("receive wa monitor request,opCode:{},criteria:{}", opCode, criteria);

		List<Data> status = new ArrayList<>();
		List<Data> monitorResult = new ArrayList<>();
		
		if (criteria.size() < 1 || criteria.size() > 2) {
			status = DataUtil.getResonseData("1", "11");
			putData2XmlAndUpload(status, null, opCode, msgId, opId);
			logger.info("the wa param was wrong,criteria:{}", criteria);
			return;
		} // param 校验

		Entry<String, String> opCodeEntry = parseOpcode(opCode);
		
		if (opCodeEntry == null) {
			status = DataUtil.getResonseData("1", "11");
			putData2XmlAndUpload(status, null, opCode, msgId, opId);
			return;
		} // opCode 校验

		String type = opCodeEntry.getKey(); // 布控类型
		String opCode2Db = opCodeEntry.getValue(); // 业务类型

		boolean hasMonitored = false;
		Entry<String, String> entry = getSize1Map(criteria);
		hasMonitored = isExist(opCode2Db, entry.getKey(), entry.getValue()); // has monitored

		// *根据布控的业务类型，进行布控操作

		if (Constant.MONITOR_ADD.equals(type)) {

			if (hasMonitored) {
				status = DataUtil.getResonseData("1", "2");
			} else {
				status = DataUtil.getResonseData("0", "0");
				insert4Criteria(opCode2Db, criteria, msgId, opId);
			}

			putData2XmlAndUpload(status, null, opCode, msgId, opId);

		} // 布控

		if (Constant.MONITOR_DEL.equals(type)) {

			if (hasMonitored) {
				status = DataUtil.getResonseData("0", "0");
				delete4Criteria(opCode2Db, criteria, msgId, opId);
			} else {
				status = DataUtil.getResonseData("1", "3");
			}

			putData2XmlAndUpload(status, null, opCode, msgId, opId);

		} // 停控
		
		if (Constant.MONITOR_STATUS.equals(type)) {
			
			long monitorTime = 0;
			if (hasMonitored) {
				monitorTime = getMonitorTime(criteria, opCode2Db);
			} // monitorTime: 状态为未控，布控时间为0
			
			monitorResult = DataUtil.getMonitorResultData(criteria, monitorTime, hasMonitored);
			
			status = DataUtil.getResonseData("0", "0");
			putData2XmlAndUpload(status, monitorResult, opCode, msgId, opId);
			
		} // 布控状态

//		if (Constant.MONITOR_STATUS.equals(type)) {
//			
//			String uid = "";
//			try {
//				uid = getUid(criteria);
//			} catch (TException e) {
//				logger.error(e.getMessage(), e);
//			} 
//			if (StringUtil.isBlank(uid)) {
//				status = DataUtil.getResonseData("1", "12");
//				putData2XmlAndUpload(status, null, opCode, msgId, opId);
//				return;
//			}// uid: 根据查询条件拿到uid，uid可能为空
//
//
//			long monitorTime = 0;
//			if (hasMonitored) {
//				monitorTime = getMonitorTime(criteria, opCode2Db);
//			} // monitorTime: 状态为未控，布控时间为0
//			
//
//			if (opCode.equals(Constant.ADD_MONITOR_CHAT_STATUS)) {
//
//				String account = "";
//				if (criteria.containsKey(Constant.DATA_USER_ACCOUNT)) {
//					account = criteria.get(Constant.DATA_USER_ACCOUNT);
//				} else {
//					account = uid;
//					account = ElasticSearchUtil.getAccountByUid(uid);
//				}// account
//
//				monitorResult = DataUtil.getMonitorResultData(account, uid, monitorTime, hasMonitored);
//			} // chart status
//
//			if (opCode.equals(Constant.ADD_MONITOR_BARRAGE_STATUS) || opCode.equals(Constant.ADD_MONITOR_MEDIA_BROWSE_LOG_STATUS)) {
//				monitorResult = DataUtil.getMonitorResultData(null, uid, monitorTime, hasMonitored);
//			} // barrage and play status 
//
//			status = DataUtil.getResonseData("0", "0");
//			putData2XmlAndUpload(status, monitorResult, opCode, msgId, opId);
//
//		} // 布控状态

	}

	private void insert4Criteria(String opCode, Map<String, String> criteria, String msgId, String opId) {
		Entry<String, String> entry = getSize1Map(criteria);
		insert(opCode, entry.getKey(), entry.getValue(), msgId, opId);
	}

	private void insert(String opCode, String conditionKey, String conditionValue, String msgId, String opId) {
		Condition condition = new Condition();
		condition.setOpcode(opCode);
		String pmId = opId + "_" + msgId;
		condition.setPmId(pmId);
		condition.setConditionKey(conditionKey);
		condition.setConditionValue(conditionValue);
		condition.setCreateAt(new Date());
		dao.insert(condition);
	}

	private void delete4Criteria(String opCode, Map<String, String> criteria, String msgId, String opId) {
		Entry<String, String> entry = getSize1Map(criteria);
		Condition condition = dao.selectByCriteria(opCode, entry.getKey(), entry.getValue());
		dao.delete(condition);
	}

	private static Entry<String, String> getSize1Map(Map<String, String> criteria) {
		Set<Entry<String, String>> entries = criteria.entrySet();
		Iterator<Entry<String, String>> iterator = entries.iterator();
		return iterator.next();
	}

	private boolean isExist(String opCode, String conditionKey, String conditionValue) {
		Condition condition = dao.selectByCriteria(opCode, conditionKey, conditionValue);
		return !(condition == null);
	}

	private void putData2XmlAndUpload(List<Data> status, List<Data> monitorResult, String opCode, String msgId,	String opId) {
		try {
			String dir = FileUtil.getMonitorXMLDir(opCode, Constant.WA_RESPONSE, msgId);
			ResponseInfo response = WaConverter.convertToCommonResponse(status, monitorResult, opCode, msgId, opId);
			OxmUtil.toXML(response.getResponse(), new File(dir));

			SFTPUploadUtil.uploadFile(SFTPUploadUtil.JINGKOU, dir);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private String getUid(Map<String, String> criteria) throws TException {

		String uid = "";

		Set<Entry<String, String>> entries = criteria.entrySet();
		for (Entry<String, String> entry : entries) {
			if (entry.getKey().equals("USER_INTENRALID")) {
				uid = entry.getValue();
			}
			if (entry.getKey().equals("USER_ACCOUNT")) {
				String value = entry.getValue();
				uid = getAccountUid(value);
			}
		}

		if (StringUtil.isBlank(uid)) {
			return null;
		}

		return uid;
	}

	private long getMonitorTime(Map<String, String> criteria, String opCode) {

		Entry<String, String> entry = getSize1Map(criteria);
		Condition condition = dao.selectByCriteria(opCode, entry.getKey(), entry.getValue());
		Date createAt = condition.getCreateAt();

		long monitorTime = new Date().getTime() - createAt.getTime();

		return monitorTime;
	}

	private String getAccountUid(String value) throws TException {

		Pattern pattern = Pattern.compile("\\d{11}");
		Matcher matcher = pattern.matcher(value);

		boolean is_email = value.contains("@");
		boolean is_mobile = matcher.matches();

		Long uid = null;

		// 确定account的类型
		if (is_email) {

			Passport passport = passportService.queryPassportByEmail(value);
			uid = passport.getUid();

		} else if (is_mobile) {

			Passport passport = passportService.queryByMobile(value);
			uid = passport.getUid();

		} else {
			return value;
		}

		if (uid == null) {
			return null;
		}

		return String.valueOf(uid);

	}

	private static Entry<String, String> parseOpcode(String opCode) {
		Map<String, String> map = new HashMap<>();

		if (opCode.startsWith("ADD") && !opCode.endsWith("STATUS")) {
			String opCode2Db = opCode.substring(3);
			map.put(Constant.MONITOR_ADD, opCode2Db);
		}
		if (opCode.startsWith("DEL")) {
			String opCode2Db = opCode.substring(3);
			map.put(Constant.MONITOR_DEL, opCode2Db);
		}
		if (opCode.startsWith("ADD") && opCode.endsWith("STATUS")) {
			String opCode2Db = opCode.substring(3, opCode.length() - 6);
			map.put(Constant.MONITOR_STATUS, opCode2Db);
		}

		if (map.size() == 1) {
			Set<Entry<String, String>> entries = map.entrySet();
			Iterator<Entry<String, String>> iterator = entries.iterator();
			return iterator.next();
		}

		return null;
	}

	public static void main(String[] args) {

		Map<String, String> criteria = new HashMap<>();
		criteria.put("USER_ACCOUNT", "111111");
		Entry<String, String> entry = getSize1Map(criteria);
		System.out.println(entry.getKey());
		System.out.println(entry.getValue());

		 System.out.println(parseOpcode("ADDMONITORMEDIABROWSELOG"));
		 System.out.println(parseOpcode("DELMONITORMEDIABROWSELOG"));
		 System.out.println(parseOpcode("ADDMONITORMEDIABROWSELOGSTATUS"));
	}

}
