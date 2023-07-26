package com.ximalaya.wa.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jcraft.jsch.JSchException;
import com.nali.common.util.CollectionUtils;
import com.ximalaya.wa.cache.GuavaCache;
import com.ximalaya.wa.converter.WaConverter;
import com.ximalaya.wa.model.CommonProcessor;
import com.ximalaya.wa.model.xml.Data;
import com.ximalaya.wa.model.xml.QueryResponse;
import com.ximalaya.wa.model.xml.ResponseInfo;
import com.ximalaya.wa.sender.model.Constant;
import com.ximalaya.wa.sender.util.FileUtil;
import com.ximalaya.wa.sender.util.SFTPUploadUtil;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.SearchResult.Hit;
import io.searchbox.core.SearchScroll;
import io.searchbox.params.Parameters;

/**
 * 
 * @author Stan.She
 * 
 * es的查询的工具类
 * 有如下功能：
 * 1.分页查询，采用scroll和search_type和post_filter来提高查询效率，以及遍历，拿到全量的数据
 * 	 这个地方有两个问题：
 * 	 a.要拿到指定量的数据，比如1w条，search_type查询出的数据量是无规律的，不能使用。
 *   b.size的值决定了每次查询的最大的数据量。最大数据量为 tableNo*shardNo*size。
 * 2.查询一条数据
 * 3.数据补全。
 * 4.批量查询。
 * 5.根据查询条件拿到uid，批量查询account表，写入缓存来提高查询数据补全的效率。
 * 
 */
@SuppressWarnings("all")
public class ElasticSearchUtil {

	private static final Logger		logger	     	= LoggerFactory.getLogger(ElasticSearchUtil.class);
	
	private static final String 	ES_PROPERTIES_FILE_NAME = "es.properties";

	public static   	 JestClient client;

	private static final int		XML_MAX_ROW	    = 10000;
	private static final int		AGGREGATION_NUM	= 1000;

	private static final String		SCROLL_TIME  	= "1h";
	
	private final static String    ORDER_DRAFT      = "event_paid_order_draft*";
	private final static String    ORDER_PRICE      = "event_paid_order_price*";

//  es version 2.2.0 测试环境
//	private static final String		serverUrl		= "http://192.168.3.68:9200/"; //测试环境

	static {
		
		List<String> nodes = new LinkedList<String>();
		
		URL url = ElasticSearchUtil.class.getClassLoader().getResource(ES_PROPERTIES_FILE_NAME);
	    try {
	         Properties properties = new Properties();
	         properties.load(url.openStream());
	            
	         String esStr = properties.getProperty("es.connectString");
	         String[] uuids = esStr.split(",");
	            
	         nodes = Arrays.asList(uuids);
	         System.out.println(nodes);
	            
	    } catch (IOException e) {
	        throw new IllegalArgumentException("can not find config file:" + ES_PROPERTIES_FILE_NAME);
	    }
		
		
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(
				new HttpClientConfig.Builder(nodes)
			   .multiThreaded(true)
			   .connTimeout(30000)
			   .readTimeout(60000)
			   .build());
		
		client = factory.getObject();
		
	}

	private static final String TYPE = "event";

	/**
	 * 注：方法不使用
	 * 10000条数据分页一次 注：es查询的分页只能通过scroll来执行，因为线上的数据量比较大的话，使用from,size的方式性能会很低
	 * 方法一设置size为1000，然后每10000条数据写入一次，引入search_type为scan后，返回数据的大小不定，每次写入的数量就不再指定了。
	 * @param index
	 * @param key
	 * @param value
	 * @return
	 */
	public static List<List<Map<String, Map<String, Object>>>> getElasticSearchResult(QueryBuilder queryBuilder,String index) {

		if (queryBuilder == null) {
			return null;
		}

		// 一个map是一条数据，内层的list是10000条数据，外层的list是指有多少个万条数据
		List<List<Map<String, Map<String, Object>>>> list = new ArrayList<>();
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(1000); // 分页
		searchSourceBuilder.query(queryBuilder);

		Search search = new Search.Builder(searchSourceBuilder.toString())
						.addIndex(index)
						.addType(TYPE)
						.setParameter(Parameters.SCROLL, SCROLL_TIME)
						.build();

		try {

			// 拿到第一次的查询结果
			JestResult result = client.execute(search);
			String scrollId = result.getJsonObject().get("_scroll_id").getAsString();

			List<Map<String, Map<String, Object>>> resultList = new ArrayList<>();
			List<Map<String, Map<String, Object>>> resultList0 = getSourceData(result);
			resultList.addAll(resultList0);

			// 遍历，使用scroll取出后面的结果，直到取出的数据为空
			while (true) {

				// 如果resultList数据量大于1w，写入到list中
				if (resultList.size() >= XML_MAX_ROW) {
					list.add(resultList);
					resultList = new ArrayList<>();
				}

				// 按照1k的偏移量查询，查询的数据写入到resultList中
				SearchScroll scroll = new SearchScroll.Builder(scrollId, SCROLL_TIME).build();
				result = client.execute(scroll);
				List<Map<String, Map<String, Object>>> resultListI = getSourceData(result);
				if (CollectionUtils.isEmpty(resultListI)) {// 结束的时候，把最后的resultList写入到list中
					System.out.println(resultList.size());
					list.add(resultList);
					break;
				}
				resultList.addAll(resultListI);
				scrollId = result.getJsonObject().get("_scroll_id").getAsString();
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return list;

	}

	
	
	/**
	 * 使用post-filter和search_type为scan来提高性能
	 * @param queryBuilder
	 * @param index
	 * @return
	 * @throws JSchException 
	 */
	public static void esSearchAndPutData2XmlAndUpload(QueryBuilder queryBuilder,String index,boolean toBeCached,String className,String opCode, String msgId) {
		
		long begin = System.currentTimeMillis();
		
		if (queryBuilder == null) {
			return;
		}
		
		logger.info("search criteria : {}", queryBuilder.toString());
		
		// => 1.先将account信息写入到缓存中，如果queryBuilder的查询条件为common.uid，不写入缓存
		// 如果数据量大，查询时间会多批量查询的时间，但会极大的提高写入的效率
		if (toBeCached) {
			List<String> uids = getListUidByQueryBuilder(queryBuilder, index);
			put2Cache4AccountWithUidInBatch(uids);
		}
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
		searchSourceBuilder.postFilter(queryBuilder);
		
//		Sort sort = new Sort("_doc");
		
		Search search = new Search.Builder(searchSourceBuilder.toString())
						.addIndex(index)
						.addType(TYPE)
//						.addSort(sort)
						.setParameter(Parameters.SCROLL, SCROLL_TIME)
						.setParameter(Parameters.SIZE, 50)
						.build();
		
		logger.info("search json : {}",search.toString());
		
		int num = 0;
		int n = 1;
		
		List<String> fileNames = new ArrayList<>(); // 生成的文件
		
		try {
			
			long time1 = System.currentTimeMillis();

			// => 2.拿到第一次的查询结果
			JestResult result = client.execute(search);
			String scrollId = result.getJsonObject().get("_scroll_id").getAsString();

			// 2.x版本 search_type为scan第一次查询是没有结果的；5.x版本去掉了scan查询，第一次查询是有结果的
			 List<Map<String, Map<String, Object>>> resultList0 = getSourceData(result);
			 
			 if (CollectionUtils.isEmpty(resultList0)) {
				
				String dir0 = putESData2XML(opCode, className, null, msgId);
				fileNames.add(dir0);
				
			}else{
				
				logger.info("first size : {}",resultList0.size()); 
				num = resultList0.size();

				String dir0 = putESData2XML(opCode, className, resultList0, msgId);
				fileNames.add(dir0);
				
				// => 3.遍历，使用scroll取出后面的结果，直到取出的数据为空
				while (true) {

					// search_type为scan后每次查询最大值为size*shardsNum
					SearchScroll scroll = new SearchScroll.Builder(scrollId, SCROLL_TIME).build();
					result = client.execute(scroll);
					
					List<Map<String, Map<String, Object>>> resultList = getSourceData(result);
					if (CollectionUtils.isEmpty(resultList)) { // 结束的时候，把最后的resultList写入到list中
						break;
					}
					
					logger.info("", resultList.size());
					
					//将数据写入XML
					long write_begin = System.currentTimeMillis();
					String dir = putESData2XML(opCode, className, resultList, msgId);
					fileNames.add(dir);
					
					long write_end = System.currentTimeMillis();
					logger.info("size => {},{} every scroll cost time => {}s",resultList.size(),opCode,(write_end - write_begin)/1000);
				
					
					num += resultList.size();
					if (num >= 10000*n) {//1w条数据打一次日志
						long time2 = System.currentTimeMillis();
						logger.info("1w data cost time : {}s,now num : {}",(time2 - time1)/1000,num);
						n++;
						time1 = time2;
					}
					
					scrollId = result.getJsonObject().get("_scroll_id").getAsString();
					
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		long end = System.currentTimeMillis();
		logger.info("this search task costs time :{}m,total num: {},index:{},\ncriteria:{}",
				(end - begin)/(1000*60),num,index,queryBuilder.toString());
		
		try {
			SFTPUploadUtil.uploadFile(SFTPUploadUtil.JINGKOU, fileNames);
		} catch (JSchException e) {
			logger.error("upload file error,fileNames:{}",fileNames);
		}
	}

	
	/**
	 * 一个订单号只等查出一个数据
	 * @param queryBuilder
	 * @param className
	 * @param opCode
	 * @param msgId 
	 * @throws JSchException
	 * 
	 */
	public static void esSearchAndPutData2XmlAndUpload4PaymentOrder(QueryBuilder queryBuilder,String className,String opCode, String msgId) {
		
		if (queryBuilder == null) {
			return;
		}
		
		logger.info("payment criteria :{}",queryBuilder.toString());
		
		List<Map<String,Map<String, Object>>> resultList = new ArrayList<>();
		
		Map<String,Map<String,Map<String, Object>>> result1 = getSourceDataByOrderId4PaymentOrder(queryBuilder,ORDER_DRAFT);
		logger.info("size from draft : {}",result1.size());
		Map<String,Map<String,Map<String, Object>>> result2 = getSourceDataByOrderId4PaymentOrder(queryBuilder,ORDER_PRICE);
		logger.info("size from price : {}",result1.size());
		
		if (result1.isEmpty()) {
			putESData2XML(opCode, className, null, msgId);
			return;
		}
		
		for (Entry<String, Map<String,Map<String, Object>>> sourceEntry : result1.entrySet()) {
			Map<String,Map<String,Object>> source1 = result1.get(sourceEntry.getKey());
			Map<String,Map<String,Object>> source2 = result2.get(sourceEntry.getKey());
			if(source2 != null && source2.size() > 0) {
				source1.get("data").putAll(source2.get("data"));
			}
			resultList.add(source1);
		}
			
		String dir = putESData2XML(opCode, className, resultList, msgId);
		
		try {
			SFTPUploadUtil.uploadFile(SFTPUploadUtil.JINGKOU, dir);
		} catch (JSchException e) {
			logger.error("upload file error,fileName:{}",dir);
		}
			
		logger.info("payment query and write task end...");
	}
	
	
	private static Map<String,Map<String,Map<String, Object>>> getSourceDataByOrderId4PaymentOrder(QueryBuilder queryBuilder,String index){
		
		Map<String,Map<String,Map<String, Object>>> map = new HashMap<>();
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(1000);//理论上专辑数不会超过1000个
		searchSourceBuilder.query(queryBuilder);

		Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(index).addType(TYPE).build();

		try {
			SearchResult result = client.execute(search);
			map = assembleSourceData4PaymentOrder(result);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return map;
	}
	
	private static Map<String,Map<String,Map<String, Object>>> assembleSourceData4PaymentOrder(SearchResult result){
		
		JsonObject rootJson = result.getJsonObject();

		JsonObject outerHits = rootJson.get("hits").getAsJsonObject();
		JsonArray innerHits = outerHits.get("hits").getAsJsonArray();
		
		if (checkNull(innerHits) || innerHits.size() == 0) {
			return null;
		}

		Map<String,Map<String, Map<String, Object>>> map = new HashMap<>();
		for (JsonElement hit : innerHits) {
			Map<String, Map<String, Object>> sourceMap = new HashMap<>();

			JsonObject obj = hit.getAsJsonObject();
			JsonObject source = obj.get("_source").getAsJsonObject();
			
			String orderId = "";
			Set<Map.Entry<String, JsonElement>> entry = source.entrySet();
			for (Map.Entry<String, JsonElement> outterEntry : entry) {
				
				if (outterEntry.getKey().equals("common") || outterEntry.getKey().equals("global")) {
					Map<String, Object> innerMap = new HashMap<>();
					for (Map.Entry<String, JsonElement> innerEntry : outterEntry.getValue().getAsJsonObject().entrySet()) {
						// 如果要将JsonPrimitive转化为基本类型，需要进行转化
						if (innerEntry.getValue().isJsonNull()) {
							innerMap.put(innerEntry.getKey(), null);
						} else {
							innerMap.put(innerEntry.getKey(), innerEntry.getValue().getAsString());
						}

					}

					sourceMap.put(outterEntry.getKey(), innerMap);
			
				}
				
				if (outterEntry.getKey().equals("data")) {
					Map<String, Object> innerMap = new HashMap<>();
					for (Map.Entry<String, JsonElement> innerEntry : outterEntry.getValue().getAsJsonObject().entrySet()) {
						// 如果要将JsonPrimitive转化为基本类型，需要进行转化
						if (innerEntry.getValue().isJsonNull()) {
							innerMap.put(innerEntry.getKey(), null);
						} else {
							innerMap.put(innerEntry.getKey(), innerEntry.getValue().getAsString());
						}
						
						if (innerEntry.getKey().equals("unified_order_no")) {
							orderId = innerEntry.getValue().getAsString();
						}
						
					}
					
					sourceMap.put(outterEntry.getKey(), innerMap);
					
				}
				
			}
			
			map.put(orderId, sourceMap);

		}

		return map;
		
	}

	private static String putESData2XML(String opCode,String className,List<Map<String,Map<String,Object>>> source, String msgId){
		
		// 根据返回值拿到errorCode值
		if (CollectionUtils.isEmpty(source)) {
			String dir = FileUtil.getQueryXMLDir(opCode, Constant.WA_RESPONSE, msgId);
			writeNullData2Xml(opCode, msgId, dir);
			return dir;
		}else{
			try {
				List<Data> datas = DataUtil.getResonseData("0", "0");
				List list = WaConverter.convertToModelList((Class<? extends CommonProcessor>) Class.forName(className),source);
				String dir = FileUtil.getQueryXMLDir(opCode, Constant.WA_RESPONSE, msgId);
				
				// => xml会过滤一些异常的信息，可能导致不写数据，不写数据应该返回未查到数据的异常
				ResponseInfo<QueryResponse> response = WaConverter.convertToQueryResponse(datas, list, msgId);
				
				if (response.getResponse() == null) writeNullData2Xml(opCode, msgId, dir);
				else OxmUtil.toXML(response.getResponse(), new File(dir));
				
				return dir;
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
			 
		}
		
		return null;
		
	}
	
	
	
	public static QueryBuilder getQueryBuilderByTermQuery(String key, String value) {
		if (value == null) {
			return null;
		}

		return QueryBuilders.termQuery(key, value);
	}

	public static QueryBuilder getQueryBuilderByBoolQuery(String key, String value, String beginTime, String endTime) {
		if (value == null) {
			return null;
		}

		String time1 = beginTime + "000";
		String time2 = endTime   + "000";
		
		return QueryBuilders.boolQuery()
				.must(QueryBuilders.termQuery(key, value))
				.must(QueryBuilders.rangeQuery("common.serverTime").gte(time1).lte(time2));
		
	}
	
	
	
	// 默认方法每次只能拿10条数据，这里用来拿到第一条数据
	public static List<Map<String, Map<String, Object>>> getSingleResultByTermType(String index, String key, String value) {

		if (value == null) {
			return null;
		}

		List<Map<String, Map<String, Object>>> list = new ArrayList<>();
		
		QueryBuilder queryBuilder = QueryBuilders.termQuery(key, value);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(1);
		searchSourceBuilder.query(queryBuilder);

		Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(index).addType(TYPE).build();

		try {
			SearchResult result = client.execute(search);
			list = getSourceData(result);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return list;
	}
	
	
	
	
	/**
	 * 转化为jsonObject拿到source
	 * @param result
	 * @return
	 */
	private static List<Map<String, Map<String, Object>>> getSourceData(JestResult result) {
		JsonObject rootJson = result.getJsonObject();

		JsonObject outerHits = rootJson.get("hits").getAsJsonObject();
		JsonArray innerHits = outerHits.get("hits").getAsJsonArray();
		
		if (checkNull(innerHits) || innerHits.size() == 0) {
			return null;
		}

		List<Map<String, Map<String, Object>>> hitSourceMapList = new ArrayList<>();
		for (JsonElement hit : innerHits) {
			Map<String, Map<String, Object>> sourceMap = new HashMap<>();

			JsonObject obj = hit.getAsJsonObject();
			JsonObject source = obj.get("_source").getAsJsonObject();
		
			Set<Map.Entry<String, JsonElement>> entry = source.entrySet();
			for (Map.Entry<String, JsonElement> outterEntry : entry) {
				
				if (outterEntry.getKey().equals("common") || outterEntry.getKey().equals("data") || outterEntry.getKey().equals("global")) {
					Map<String, Object> innerMap = new HashMap<>();
					for (Map.Entry<String, JsonElement> innerEntry : outterEntry.getValue().getAsJsonObject().entrySet()) {
						// 如果要将JsonPrimitive转化为基本类型，需要进行转化
						if (innerEntry.getValue().isJsonNull()) {
							innerMap.put(innerEntry.getKey(), null);
						} else {
							innerMap.put(innerEntry.getKey(), innerEntry.getValue().getAsString());
						}

					}

					sourceMap.put(outterEntry.getKey(), innerMap);
			
				}
				
			}

			hitSourceMapList.add(sourceMap);
		}

		return hitSourceMapList;

	}

	/**
	 *  jest hit对象拿到source，会将所有的数字转化为double或者科学技术法
	 * @param result
	 * @return
	 */
	private static List<Map> getSourceData1(SearchResult result) {

		String json = result.getJsonString();
		result.setSucceeded(true);
		result.setJsonString(json);
		result.setJsonObject(new JsonParser().parse(json).getAsJsonObject());
		result.setPathToResult("hits/hits/_source");

		List<Hit<Object, Object>> hits = result.getHits(Object.class, Object.class);

		if (CollectionUtils.isEmpty(hits)) {
			return null;
		}

		List<Map> list = new ArrayList<>();
		for (Hit<Object, Object> hit : hits) {
			Map map = (Map) hit.source;
			map.remove(SearchResult.ES_METADATA_ID);
			list.add(map);
		}

		return list;
	}
	
	
	// 根据uid拿到account的逻辑
	public static String getAccountByUid(String uid) {
		
		List<Map<String, Map<String, Object>>> sources = getSingleResultByTermType("event_user_profile", "common.uid", uid);
		
		if (CollectionUtils.isEmpty(sources)) {
			return uid;
		}

		Map<String, Map<String, Object>> map = sources.get(0);
		Map<String, Object> data = map.get("data");

		String account = uid;

		Object mobile = data.get("currentMobile");

		if (!checkNull(mobile)) {
			account = String.valueOf(mobile);
		} else {
			
			Object email = data.get("currentEmail");
			if (!checkNull(email)) {
				account = String.valueOf(email);
			} else {
				account = uid;
			}
			
		}

		return account;
	}
	
	
	/**
	 * 传入为source的JsonObject，拿到uid和account的map
	 * @param source
	 * @return
	 */
	public static Map<String,String> getAccountMapBySourceData(JsonObject source){
		Map<String,String> map = new HashMap<>();
		
		if (source.isJsonNull()) {
			return null;
		}
		
		JsonObject data = source.get("data").getAsJsonObject();
		
		String uid = data.get("uid").getAsString();
		
		String account = uid;
		
		JsonElement currentMobile = data.get("currentMobile");
		if (!checkNull(currentMobile)) {
			account = currentMobile.getAsString();
		}else{
			JsonElement currentEmail = data.get("currentEmail");
			if (!checkNull(currentEmail)) {
				account = currentEmail.getAsString();
			}
		}
		
		map.put(uid,account);
		
		return map;
	}
	
	/**
	 * 每次查询AGGREGATION_NUM的数据
	 * @param uids
	 */
	private static void put2Cache4AccountWithUidInBatch(List<String> uids){
		
		long begin = System.currentTimeMillis();
		
		// uids中删除缓存中已经存在的uid，不需要再查
		List<String> queryUids = GuavaCache.removeExistUid(uids);
		if (CollectionUtils.isEmpty(queryUids)) {
			return;
		}
		logger.info("after remove from cache,now queryUids size : {}" , queryUids.size());
		
		
		int count = 0;
		
		while (count < queryUids.size()) {
			List<String> everyQueryUids = queryUids.subList(count, (count + AGGREGATION_NUM > queryUids.size() ? queryUids.size() : count + AGGREGATION_NUM));
			put2Cache4AccountWithUidInSingleQuery(everyQueryUids);
			count += AGGREGATION_NUM;
		}
		
		long end = System.currentTimeMillis();
		logger.info("aggregation task cost time : {}s",(end - begin)/1000);
		
	}
	
	/**
	 * es聚合查询（相当于group by + having的查询）
	 * 并返回key与json的唯一对应
	 * 再由uid求得account写入到guaua cache中。
	 * @param uids
	 */
	private static void put2Cache4AccountWithUidInSingleQuery(List<String> uids){
		
		QueryBuilder queryBuilder = QueryBuilders.termsQuery("common.uid",uids);
		
		TermsAggregationBuilder termBuilder = AggregationBuilders.terms("common_uid_agg").field("common.uid").size(AGGREGATION_NUM)
								   .subAggregation(AggregationBuilders.topHits("max_hits").size(1));
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);
		searchSourceBuilder.query(queryBuilder).aggregation(termBuilder);
//		logger.info("aggregation search json to es : {}",searchSourceBuilder.toString());
		logger.info("the size of aggregation search uids: {}",uids.size());
		
		Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("event_user_profile").addType(TYPE).build();		
		
		try {
			
			SearchResult result = client.execute(search);
			
			// 解析json并且写入到缓存中
			JsonObject obj = result.getJsonObject();
			JsonArray buckets = result.getJsonObject()
								.get("aggregations").getAsJsonObject()
								.get("common_uid_agg").getAsJsonObject()
								.get("buckets").getAsJsonArray();
			
		   for (JsonElement jsonElement : buckets) {
			   JsonArray hits  = jsonElement.getAsJsonObject()
					   			   .get("max_hits").getAsJsonObject()
					   			   .get("hits").getAsJsonObject()
					   			   .get("hits").getAsJsonArray();
			  
			   if (checkNull(hits) || hits.size() == 0) {
					continue;
				}
			   
			   JsonObject source = hits.get(0).getAsJsonObject().get("_source").getAsJsonObject();

			   Map<String,String> map = getAccountMapBySourceData(source);
			   GuavaCache.putData(map);
		   
		   }
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	
	 /**
	  *  根据查询条件拿到该查询条件下所有的uid，因为这些uid在写入到xml中的时候需要数据补全
	  * @param queryBuilder
	  * @param index
	  * @return
	  */
	private static List<String> getListUidByQueryBuilder(QueryBuilder queryBuilder,String index){

		long begin = System.currentTimeMillis();
		
//		Sort sort = new Sort("_doc");
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().storedField("common.uid");
		searchSourceBuilder.query(queryBuilder);
		Search search = new Search.Builder(searchSourceBuilder.toString())
						.addIndex(index).addType(TYPE)
//						.addSort(sort)
						.setParameter(Parameters.SCROLL, SCROLL_TIME)
						.setParameter(Parameters.SIZE, 50)
						.build();
		
		List<String> resultUids = new ArrayList<>(); // 返回
		
		try {
			JestResult result = client.execute(search);
			String scrollId = result.getJsonObject().get("_scroll_id").getAsString();
			
			boolean isReturned = handleUids(result, resultUids);
			if (isReturned) return null;
			
			long time1 = System.currentTimeMillis();
			int num = 0;
			int n = 1;
			
			while (true) {

				// search_type为scan后每次查询最大值为size*shardsNum
				SearchScroll scroll = new SearchScroll.Builder(scrollId, SCROLL_TIME).build();
				result = client.execute(scroll);
				
				isReturned = handleUids(result, resultUids);
				if (isReturned) break;
				
				if (resultUids.size() >= 10000*n) {//1w条数据打一次日志
					long time2 = System.currentTimeMillis();
					logger.info("field task 1w data cost time : {}s,now num : {}",(time2 - time1)/1000,num);
					n++;
					time1 = time2;
				}
				
				scrollId = result.getJsonObject().get("_scroll_id").getAsString();
				
			}
			
			long end = System.currentTimeMillis();
			
			logger.info("field task cost time : {}s,total uid : {}",(end - begin)/1000,num);
			
			return resultUids;
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		
		return null;
	}
	
	private static boolean handleUids(JestResult result,List<String> resultUids){
		
		boolean isReturned = false;
		
		List<String> uids = getListUid(result);
		if (uids == null) return true;
		
		for (String uid : uids) {
			if (!resultUids.contains(uid)) {
				resultUids.add(uid);
			}
		}
		
		return false;
	}
	
	/**
	 * 根据fields查询拿到common.uid中的uid
	 * @param result
	 * @return
	 */
	private static List<String> getListUid(JestResult result){
		
		JsonObject outerHits = result.getJsonObject().get("hits").getAsJsonObject();
		JsonArray innerHits = outerHits.get("hits").getAsJsonArray();
		
		if (checkNull(innerHits) || innerHits.size() == 0) {
			return null;
		}
		
		List<String> uids = new ArrayList<>();
		
		for (JsonElement hit : innerHits) {
			
			JsonObject innerHit = hit.getAsJsonObject();
			JsonElement fields = innerHit.get("fields");
			
			if (!checkNull(fields)) {
				JsonArray uidJson = fields.getAsJsonObject().get("common.uid").getAsJsonArray();
				String uid = uidJson.getAsString();
				if (!uids.contains(uid)) {
					uids.add(uid);
				}
			}
		}
		
		return uids;
	}

	
	
	// JsonNull和空字符串的判断
	private static boolean checkNull(Object o) {

		if (null == o)
			return true;
		
		// JsonNull对象的判断
		// 方法一：JsonNull.INSTANCE.equals(o)
		// 方法二：o.toString().equals("null")
		if ("null".equals(o.toString())) return true;

		// 空字符串("")判断，空字符串是com.google.gson.JsonPrimitive类型：
		// 方法一：System.out.println(o.getClass());
		// 方法二：可以用hashCode为0判断
		// 方法三：o.toString().equals("\"\"")判断
		// 方法四：o.toString().hashCode() == 1288判断
		if (o.hashCode() == 0) return true;

		return false;

	}
	
	private static void writeNullData2Xml(String opCode,String msgId,String dir){
		try {
			List<Data> datas = DataUtil.getResonseData("1", "12");
			OxmUtil.toXML(WaConverter.convertToCommonResponse(datas, null, opCode, msgId, null).getResponse(), new File(dir));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	public static void main(String[] args) throws JSchException {
		
		System.out.println(getQueryBuilderByBoolQuery("common.uid", "1111", "1528646400", "1528732799").toString());
		
		//聚合查询
//		List<String> uids = new ArrayList<>();
//		uids.add("1049362");
//		uids.add("81777443");
//		QueryBuilder queryBuilder = QueryBuilders.termsQuery("common.uid", uids.toArray());
//		TermsBuilder termBuilder = AggregationBuilders.terms("data__uid_agg").field("data.uid").subAggregation(AggregationBuilders.topHits("max_hits").setSize(1));
//		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);
//		searchSourceBuilder.query(queryBuilder).aggregation(termBuilder);
//		System.out.println(searchSourceBuilder.toString());
//		Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("event_user_profile").addType(TYPE).build();
//		

		
		//json查询
//		String json = "{\"query\":{\"filtered\":{\"query\":{\"terms\":{\"common.uid\":[\"1049362\",\"81777443\"]}}}},\"size\":0,\"aggs\":{\"data_uid_agg\":{\"terms\":{\"field\":\"common.uid\"},\"aggs\":{\"max_hits\":{\"top_hits\":{\"size\":1}}}}}}";
//		Search search = new Search.Builder(json).addIndex("event_user_profile").addType(TYPE).build();
		
		
		//返回指定字段 fields
//		QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
//		
//		Sort sort = new Sort("_doc");
//		
//		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().storedField("common.uid");
//		searchSourceBuilder.query(queryBuilder);
//		System.out.println(searchSourceBuilder.toString());
//		Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("event_user_profile").addType(TYPE)
//				.addSort(sort)
//				.setParameter(Parameters.SCROLL, SCROLL_TIME)
//				.setParameter(Parameters.SIZE, 50).build();
//		System.out.println(search.toString());
//		
//		//通用代码，执行es查询
//		try {
//			SearchResult result = client.execute(search);
//			System.out.println(result.getJsonString());
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
		
		
//		List<String> uids = new ArrayList<>();
//		uids.add("1049362");
//		uids.add("81777443");
//		//put2Cache4AccountWithUidInBatch(uids);
			
//		QueryBuilder queryBuilder = QueryBuilders.termQuery("data.giftName", "棒棒糖");
//		List<String> uids = getListUidByQueryBuilder(queryBuilder, "event_reward*");
		
//		QueryBuilder queryBuilder = QueryBuilders.termQuery("data.trackId", "38012718");
//		List<String> uids = getListUidByQueryBuilder(queryBuilder, "event_play*");
//		System.out.println(uids.size());
//		put2Cache4AccountWithUidInBatch(uids);
		
//		System.out.println(getQueryBuilderByBoolQuery("common.uid", "1234567", "122487", "123499"));
		
//		QueryBuilder queryBuilder = QueryBuilders.termQuery("data.unified_order_no", "2016052402005726623623");
////		System.out.println(getSourceDataByOrderId4PaymentOrder(queryBuilder,"event_paid_order_draft"));
//		esSearchAndPutData2XmlAndUpload4PaymentOrder(queryBuilder, "com.ximalaya.wa.model.PaymentOrder", "QUERYPAYMENTORDER","1111111111");
//		
//		System.out.println("------the end----");
		
	}
	
	


}
