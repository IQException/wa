package com.ximalaya.wa.service.impl;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.thrift.TException;
import org.elasticsearch.index.query.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.ximalaya.passport.thrift.Passport;
import com.ximalaya.passport.thrift.RemotePassportService;
import com.ximalaya.wa.cache.GuavaCache;
import com.ximalaya.wa.converter.WaConverter;
import com.ximalaya.wa.dao.QueryDao;
import com.ximalaya.wa.model.xml.Data;
import com.ximalaya.wa.query.model.QueryCondition;
import com.ximalaya.wa.sender.model.Constant;
import com.ximalaya.wa.sender.model.ModelMapping;
import com.ximalaya.wa.sender.model.WAMapping;
import com.ximalaya.wa.sender.util.FileUtil;
import com.ximalaya.wa.sender.util.SFTPUploadUtil;
import com.ximalaya.wa.service.IWAQueryService;
import com.ximalaya.wa.util.DataUtil;
import com.ximalaya.wa.util.ElasticSearchUtil;
import com.ximalaya.wa.util.OxmUtil;

@SuppressWarnings("all")
@Service
public class WAQueryService implements IWAQueryService {

	private final Logger				logger	= LoggerFactory.getLogger(WAQueryService.class);

	@Value("${query.dir}")
	private String						QUERY_DIR;

	@Autowired
	private WAMapping					waMapping;

	@Autowired
	private ModelMapping				modelMapping;

	@Autowired
	private RemotePassportService.Iface	passportService;
	
	@Autowired
	private QueryDao                    queryDao;
	
	private List<String> uidKeys = Arrays.asList("USER_ACCOUNT","USERID");

	@Override
	public void query(String opCode, String msgId, Map<String, String> criteria) {

		logger.info("receive wa query request,opCode:{},criteria:{}", opCode, criteria);
		// => 存表
		queryDao.insert(getCondition(opCode, criteria));

		// => 参数校验
		if (!checkParam(msgId,opCode, criteria)) return;

		// => 根据业务类型拿到index和对应的model
		String index = waMapping.getIndexMap().get(opCode);
		String className = modelMapping.getModelMap().get(opCode);
		logger.info("index => {},className => {}",index,className);

		// 根据查询条件拿到es查询条件
		String key = getConditionKey(criteria);         /* 网安查询key */
		String esKey = waMapping.getDataMap().get(key); /* es查询key */
		
		String esValue = null;   						/* es查询value */
		QueryBuilder queryBuilder = null;

		if (uidKeys.contains(key)) {
			try {
				esValue = getAccountUid(criteria.get(key));
			} catch (TException e) {
				logger.error("transform account to uid error,account : {}", criteria.get(key));
			}
		} else if (key.equals("FILEMD5")){
			
		}else {
			esValue = criteria.get(key);
		} // esValue
		logger.info("esKey => {},esValue => {}",esKey,esValue);

		if (criteria.size() == 1) {
			queryBuilder = ElasticSearchUtil.getQueryBuilderByTermQuery(esKey, esValue);
			esSearchAndPutData2Xml(queryBuilder,index,esKey,className,opCode,msgId);
		}
		if (criteria.size() == 3) {
			
			// 查询条件参数有三个，说明是按照时间来查询
			// 时间跨度太大的es查询很吃内存，要将时间跨度划小一些
			// 10天，或者一个月
			
			try {
				
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			String beginTime = criteria.get(Constant.DATA_BEGINTIME);
			String endTime = criteria.get(Constant.DATA_ENDTIME);
			queryBuilder = ElasticSearchUtil.getQueryBuilderByBoolQuery(esKey, esValue, beginTime, endTime);
			esSearchAndPutData2Xml(queryBuilder,index,esKey,className,opCode,msgId);
		} // queryBuilder


	}
	
	private String getConditionKey(Map<String,String> criteria){
		
		for (Entry<String, String> entry : criteria.entrySet()) {
			if (!entry.getKey().contains("TIME")) {
				return  entry.getKey();
			}
		}
		
		return null;
	}
	 
	
	/**
	 * // 参数校验
	 * @param msgId
	 * @param opCode
	 * @param criteria
	 * @return
	 */
	private boolean checkParam(String msgId,String opCode,Map<String,String> criteria){
		
		if (criteria.size() < 1 || criteria.size() > 3) {
			writeIvaildData2Xml(msgId, opCode, "11");
			logger.error("============== wrong param,criteria : {}",criteria);
			return false;
		} 
				
		String key = getConditionKey(criteria);
		if (key == null) {
			writeIvaildData2Xml(msgId, opCode, "11");
			logger.error("============== wrong param,criteria : {}",criteria);
			return false;
		}
		
		String esKey = waMapping.getDataMap().get(key);
		if (!key.equals("FILEMD5") && esKey == null) {
			writeIvaildData2Xml(msgId, opCode, "12");
			logger.error("============== no es field, criteria : {}",criteria);
			return false;
		}
				
		String index = waMapping.getIndexMap().get(opCode);
		if (index == null) {
			writeIvaildData2Xml(msgId, opCode, "12");
			logger.error("============= no index,opCode : {}",opCode);
			return false;
		}
				
		String className = modelMapping.getModelMap().get(opCode);
		if (className == null) {
			writeIvaildData2Xml(msgId, opCode, "12");
			logger.error("============= no model,opCode : {}",opCode);
			return false;
		}
		
		return true;
	}
	
	
	
	/**
	 * 校验失败，返回错误信息
	 * 11 参数校验失败
	 * 12 查询无结果
	 * 99 系统异常 
	 * @param msgId
	 * @param opCode
	 * @param errorCode
	 */
	private void writeIvaildData2Xml(String msgId,String opCode,String errorCode){
		
		try {
			List<Data> datas = DataUtil.getResonseData("1", errorCode);
			String dir = FileUtil.getQueryXMLDir(opCode, Constant.WA_RESPONSE, msgId);
			OxmUtil.toXML(WaConverter.convertToCommonResponse(datas, null, opCode, msgId, null).getResponse(), new File(dir));
			SFTPUploadUtil.uploadFile(SFTPUploadUtil.JINGKOU, dir);
		}  catch (Exception e) {/**/}
		
	}
	
	
	public void esSearchAndPutData2Xml(QueryBuilder queryBuilder,String index,String esKey,String className,String opCode,String msgId){
		try {
			long begin = System.currentTimeMillis();

			if (opCode.equals("QUERYPAYMENTORDER")) {
				ElasticSearchUtil.esSearchAndPutData2XmlAndUpload4PaymentOrder(queryBuilder, className, opCode, msgId);
			} else {
				boolean toBeCached = !esKey.equals("common.uid") && !esKey.equals("data.uid");
				ElasticSearchUtil.esSearchAndPutData2XmlAndUpload(queryBuilder, index, toBeCached, className, opCode, msgId);
			}

			long end = System.currentTimeMillis();
			logger.info("query task costs time : {}s", (end - begin) / 1000);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
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
	

	private QueryCondition getCondition(String opCode,Map<String,String> criteria){
		
		QueryCondition condition = new QueryCondition();
		condition.setOpcode(opCode);
		condition.setCriteria(JSON.toJSONString(criteria));
		condition.setCreatedAt(new Date());
		
		return condition;
	}
	
}
