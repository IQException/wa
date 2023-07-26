//package wa;
//
//import static org.junit.Assert.fail;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.alibaba.fastjson.JSON;
//import com.ximalaya.wa.config.Dict;
//import com.ximalaya.wa.converter.WaConverter;
//import com.ximalaya.wa.model.Comment;
//import com.ximalaya.wa.model.xml.CommonData;
//import com.ximalaya.wa.model.xml.Data;
//import com.ximalaya.wa.model.xml.ManageResult;
//import com.ximalaya.wa.util.DataUtil;
//import com.ximalaya.wa.util.ElasticSearchUtil;
//import com.ximalaya.wa.util.OxmUtil;
////@RunWith(value=SpringJUnit4ClassRunner.class)
////@ContextConfiguration(value={"classpath:application-context.xml"})
//public class TestCollect {
//
//    private Logger logger = LoggerFactory.getLogger("ww");
//	@Test
//	public void test() {
//		logger.info("Not yet implemented");
//	}
//
//	@Test
//	public void testXML(){
//		///Users/nali/tmp
//		Map result = JSON.parseObject("{\"common\":{\"carrierOperator\":\"中国联通\",\"deviceType\":\"HUAWEI NXT-AL10\",\"networkMode\":\"4G\",\"os\":\"Android24\",\"ip\":\"140.207.21.180\",\"latitude\":31.15359878540039,\"channel\":\"and-d3\",\"userAgent\":\"\",\"version\":\"6.3.0\",\"deviceId\":\"ffffffff-fd65-f254-9c1c-9630003652ee\",\"deviceName\":\"android\",\"manufacturer\":\"HUAWEI\",\"responseCode\":\"200\",\"uid\":30067331,\"macAddress\":\"AgAAAAAA\",\"width\":1080,\"imei\":\"860076036313046\",\"serverTime\":1496902124884,\"height\":1812,\"longitude\":121.5674819946289},\"data\":{\"trackId\":\"36923277\",\"commentId\":\"\",\"content\":\"@淘米睡:太幼稚了吧\"},\"global\":{\"playResource\":\"\",\"resource\":\"xm_source:homepage\",\"clientTime\":1496902119955,\"xClientPort\":\"2199\",\"tid\":\"685623354590369548\",\"traffic\":\"\"},\"props\":{\"uid\":\"30067331\",\"x-span-source\":\"x-java\",\"x-server-costtime\":\"105\",\"token\":\"20e84e5c003e00419808d590f1d4caf001a8\"}}");
//		try {
//			Comment comment = WaConverter.convertToModel(Comment.class, result);
//			List<Comment> comments = new ArrayList<Comment>();
//			comments.add(comment);
////			ReportResponse response = waConverter.convertToReportResponse(Comment.class, comments);
////			OxmUtil.toXML(response, new File("/Users/nali/tmp/report"));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	@SuppressWarnings("all")
//	@Test
//	public void testXml2() throws Exception{
//		List<Map<String,Map<String,Object>>> list = ElasticSearchUtil.getSingleResultByTermType("wa-comment","common.uid","1");
////		String json = result.getJsonString();
////		result.setSucceeded(true);
////		result.setJsonString(json);
////		result.setJsonObject(new JsonParser().parse(json).getAsJsonObject());
////		result.setPathToResult("hits/hits/_source");
////		
////		List<Hit<Object, Object>> hits = result.getHits(Object.class, Object.class);
////		List<Map> list = new ArrayList<>();
////		
////		for (Hit<Object, Object> hit : hits) {
////			Map map = (Map) hit.source;
////			map.remove(SearchResult.ES_METADATA_ID);
////			list.add(map);
////		}
//
//		
//		List<Data> datas = DataUtil.getResonseData("1", "3");
//		
//		List comments = WaConverter.convertToModelList(Comment.class,list);
//		OxmUtil.toXML(WaConverter.convertToQueryResponse(datas, comments,"11111").getResponse(), new File("/Users/nali/Desktop/wangwei.xml"));
//
//	}
//	
//	@Test
//	public void testManageResult() throws Exception{
//		String opCode = "FREEZEIPZONE";
//		String opId = "1";
//		Map<String,String> criteria = new HashMap<>();
//		criteria.put("START_IP", "1.1.1.1");
//		criteria.put("END_IP", "255.255.255.254");
//		List<Data> resultData = DataUtil.getManageResultData("0","b","c");
//        CommonData commonResult = WaConverter.getCommonData(opCode,Dict.MSG_RESULT, opId);
//
//		OxmUtil.toXML(new ManageResult(commonResult.getCommonDatas(), resultData), new File("/Users/nali/Desktop/wangwei2.xml"));;
//		
//		
//		
//	}
//}
