package wa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jcraft.jsch.JSchException;
import com.ximalaya.wa.config.BizType;
import com.ximalaya.wa.converter.WaConverter;
import com.ximalaya.wa.model.CommonProcessor;
import com.ximalaya.wa.model.Payment;
import com.ximalaya.wa.model.PaymentOrder;
import com.ximalaya.wa.model.Recharge;
import com.ximalaya.wa.model.xml.Data;
import com.ximalaya.wa.model.xml.MonitorResult;
import com.ximalaya.wa.model.xml.QueryResponse;
import com.ximalaya.wa.model.xml.ResponseInfo;
import com.ximalaya.wa.model.xml.ResultInfo;
import com.ximalaya.wa.sender.model.Constant;
import com.ximalaya.wa.sender.model.ModelMapping;
import com.ximalaya.wa.sender.model.WAMapping;
import com.ximalaya.wa.sender.util.SFTPUploadUtil;
import com.ximalaya.wa.sender.util.ZipCompressUtil;
import com.ximalaya.wa.util.DataUtil;
import com.ximalaya.wa.util.OxmUtil;

/**
 * es json写xml测试
 * @author nali
 *
 */
@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:application-context-test.xml"})
public class TestEsPutData2Xml {
	
	@Autowired
	private WAMapping					waMapping;

	@Autowired
	private ModelMapping				modelMapping;
	
	/**
	 * account:fixInfo需要注释一些代码，调用了远程的服务
	 */
	@Test
	public void test(){
		String json = "{\"_scroll_id\":\"DnF1ZXJ5VGhlbkZldGNoFAAAAAAONVsDFnRsdFdzY3JfUl8yVmdOUnNPTGoxSWcAAAAADsJ4rBZQWlB6TGxGV1FZbWxaaDRxN3JHQmR3AAAAABCJC3kWM012bjFvOUdSVks2VEF2M1A5QzVkZwAAAAAOmuZ9FmVvRFotVldhUTEtR0REWHBOM1N0LXcAAAAACoSP6RZwNHJmNVF4blJrR1hvSi1aSlF6VkRBAAAAAAnvdioWRkVDWVRzQ3FRZjJ0anpIVmhoTE50dwAAAAAONVsEFnRsdFdzY3JfUl8yVmdOUnNPTGoxSWcAAAAADpyxMhZNdlNVLXZRbFI0TzRRTGpZdmJ2Vy13AAAAAA5CrBMWZDFrVE9JOEJTeFN5cFlETkxNM0xVdwAAAAAPrcSGFk05QWlyczBkUlRXOFBDNm93NzI1TEEAAAAAEK_CpxZCbXhJcXdnUlRocXBCWnJaZWw0dVdBAAAAAA9UIEgWYWZFd09sQnRRbGF6UlVJdlBoRnkzZwAAAAAOnLEzFk12U1UtdlFsUjRPNFFMall2YnZXLXcAAAAADsJ4rRZQWlB6TGxGV1FZbWxaaDRxN3JHQmR3AAAAAAl1HZsWc1JPNkNHSklUcktKVnZvclJkdGh6UQAAAAAOoJUaFmhyUG5VaHFrVDhDZllvcHlvY3FSWkEAAAAAEJ8qcRZneW1uR3AzclJVbUkzZlBLY1pxSjRRAAAAABD5x1UWUkFORE43aE1RUG1oYVlSSk9jdjdhZwAAAAAQr8KoFkJteElxd2dSVGhxcEJaclplbDR1V0EAAAAAERYyxxZwaTlHVnNsY1JGMk85V2F3WTM1QTFn\",\"took\":22,\"timed_out\":false,\"_shards\":{\"total\":20,\"successful\":20,\"failed\":0},\"hits\":{\"total\":1,\"max_score\":15.122308,\"hits\":[{\"_index\":\"event_user_profile\",\"_type\":\"event\",\"_id\":\"1025873\",\"_score\":15.122308,\"_source\":{\"data\":{\"created_at\":1356843643000,\"updated_at\":1356843643000,\"currentEmail\":\"jitao2011@sina.com\",\"nickName\":\"泡茶\",\"uid\":1025873,\"account\":\"jitao2011@sina.com\",\"registerEmail\":\"jitao2011@sina.com\",\"registerAreaCode\":\"0000000\"},\"common\":{\"areaCode\":\"0000000\",\"serverTime\":1356843643000,\"uid\":1025873}}}]}}";
		 Gson gson = new Gson();    
		 JsonObject rootJson = gson.fromJson(json, JsonObject.class);

		JsonObject outerHits = rootJson.get("hits").getAsJsonObject();
		JsonArray innerHits = outerHits.get("hits").getAsJsonArray();

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
		
		String opCode = "QUERYACCOUNT";
		String msgId = "3101161522734412068";
		String queryDir = "/Users/nali/Desktop/tmp/query/";
		
		try {
			List<Data> datas = DataUtil.getResonseData("0", "0");
			
			@SuppressWarnings({ "rawtypes", "unchecked" })
			List list = WaConverter.convertToModelList((Class<? extends CommonProcessor>) Class.forName("com.ximalaya.wa.model.Account"),hitSourceMapList);
			String fileName = Joiner.on('_').join(new String[] { Constant.APPLICATION_CODING, opCode, Constant.WA_RESPONSE, msgId,
					String.format("%04d", (int) (1 + Math.random() * 9998)), "V2.xml" });
			String dir = queryDir + fileName;
			ResponseInfo<QueryResponse> response = WaConverter.convertToQueryResponse(datas, list, msgId);
			
			if (response.getResponse() == null) {
				List<Data> datas2 = DataUtil.getResonseData("1", "12");
				OxmUtil.toXML(WaConverter.convertToCommonResponse(datas2, null, opCode, msgId, null).getResponse(), new File(dir));
			}else{
				OxmUtil.toXML(response.getResponse(), new File(dir));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@Test
	public void test2(){
		
		String opCode = "RELATIONACCOUNTINFO";
		String index = waMapping.getIndexMap().get(opCode);
		String className = modelMapping.getModelMap().get(opCode);
		System.out.println(index + "," + className);
	}
	
	@Test 
	public void test3() throws Exception{
		
//		String json = "{\"common\":{\"areaCode\":\"000000\",\"serverTime\":1527484860,\"uid\":112808024},\"data\":{\"album_id\":\"[1010600300000000086]\",\"discount_amount\":\"1\",\"goods_name\":\"\",\"goods_price\":\"[1]\",\"uid\":\"112808024\",\"unified_order_no\":\"2018052804000102400082882989\",\"user_type\":\"1\"}}";
//		Map<String, Object> map = JSON.parseObject(json, new TypeReference<Map<String, Object>>() {});
//		PaymentOrder paymentOrder = WaConverter.convertToModel(PaymentOrder.class, map);
//		System.out.println(JSON.toJSONString(paymentOrder));
		
//		String json2 = "{\"common\":{\"areaCode\":\"434209\",\"deviceId\":\"00000000-37d7-59a1-d62a-a7de003652ee\",\"deviceType\":\"ANE-AL00\",\"imei\":869713039167448,\"ip\":\"113.57.247.121\",\"latitude\":30.68897247314453,\"longitude\":113.92545318603516,\"macAddress\":\"f0:0f:ec:b5:01:0e\",\"os\":\"Android26\",\"port\":4456,\"serverTime\":1527488465,\"uid\":110839101},\"data\":{\"amount\":0.15,\"channel_type_id\":\"[8]\",\"pay_status\":\"3\",\"payee_id\":89778249,\"payer_id\":1,\"unified_order_no\":\"2018052802020110100082913411\"}}";
//		Map<String, Object> map2 = JSON.parseObject(json2, new TypeReference<Map<String, Object>>() {});
//		Payment payment = WaConverter.convertToModel(Payment.class, map2);
//		System.out.println(JSON.toJSONString(payment));
		
		String json3 = "{\"common\":{\"serverTime\":\"1519833898781\",\"areaCode\":\"433505\",\"deviceId\":\"ffffffff-835e-15ef-eb25-97046436d6ee\",\"deviceType\":\"vivo X9L\",\"imei\":\"864738033821562\",\"ip\":\"112.5.44.244\",\"latitude\":24.731807708740234,\"longitude\":118.6413345336914,\"macAddress\":\"70:d9:23:14:e4:c4\",\"os\":\"Android25\",\"port\":2880,\"uid\":101858889},\"data\":{\"amount\":18,\"pay_channel\":\"1\",\"unified_order_no\":\"2018052800140051421714\",\"user_id\":101858889}}";
		Map<String, Object> map3 = JSON.parseObject(json3, new TypeReference<Map<String, Object>>() {});
		Recharge recharge = WaConverter.convertToModel(Recharge.class, map3);
		recharge.setPmId("1111_22222");
		
		List<Recharge> datas = Collections.singletonList(recharge);
		
//		ResultInfo result = WaConverter.convertToResult(BizType.REPORT, datas, null);
		List<ResultInfo<MonitorResult>> results = WaConverter.convertToMonitorResult(datas);
		
		
		String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		
		String tempFile = "/Users/nali/Desktop/test.xml";
		OutputStream os = new FileOutputStream(tempFile);
		os.write((HEADER + System.getProperty("line.separator", "\r\n")).getBytes());
		
		for (ResultInfo<MonitorResult> resultInfo : results) {
			OxmUtil.toXML(resultInfo.getResult(), os);
		}
		
		System.out.println(JSON.toJSONString(recharge));
		
	}
	
	@Test
	public void test4() throws JSchException{
		String dir1 = "/Users/nali/Desktop/zip/tmp1/";
		String dir2 = "/Users/nali/Desktop/zip/tmp2/";
		ZipCompressUtil.zipCompressDir(dir1, "1390008_ACCOUNT_3_3101161527618576836_3470_V2.zip");
//		ZipCompressUtil.zipCompressDir(dir2, "1390008_ACCOUNT_3_3101161527618576836_3471_V2.zip");
		SFTPUploadUtil.uploadFileInBatch(SFTPUploadUtil.REPORT, "/Users/nali/Desktop/tmp/zip/");
		
	}
}
