package com.ximalaya.wa.sender.task;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.ximalaya.wa.converter.WaConverter;
import com.ximalaya.wa.model.xml.Data;
import com.ximalaya.wa.model.xml.ResponseInfo;
import com.ximalaya.wa.sender.model.Constant;
import com.ximalaya.wa.sender.model.WAResponseResult;
import com.ximalaya.wa.sender.util.FileUtil;
import com.ximalaya.wa.sender.util.SFTPUploadUtil;
import com.ximalaya.wa.service.IWAManageService;
import com.ximalaya.wa.service.IWAMonitorService;
import com.ximalaya.wa.service.IWAQueryService;
import com.ximalaya.wa.util.DataUtil;
import com.ximalaya.wa.util.OxmUtil;
import com.ximalaya.wa.util.WAXmlParseUtil;

@Component
public class AchieveWATask implements InitializingBean, DisposableBean {

	private final Logger		logger	= LoggerFactory.getLogger(AchieveWATask.class);

	@Value("${channel_name}")
	private int					channel_name;

	@Value("${channel_password}")
	private String				channel_password;

	@Value("${client_ip}")
	private String				client_ip;

	@Value("${host}")
	private String				host;

	@Value("${port}")
	private int					port;

	@Autowired
	private IWAQueryService		queryService;

	@Autowired
	private IWAManageService	manageService;

	@Autowired
	private IWAMonitorService	monitorService;

	/**
	 * 循环发送”接收请求”到读码交换机外网端。 在“接收响应”返回”无数据”（代码“3”）的情况下，等待1秒。
	 * 在”接收响应”返回”成功”（代码“0”）的情况下，继续发送”接收请求”。
	 */
	@Override
	public void destroy() throws Exception {

	}

	@Override
	public void afterPropertiesSet() throws Exception {

		logger.info("start to receive wa message...");

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {

				RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(30000)
						.setConnectTimeout(30000).setConnectionRequestTimeout(30000)
						.setStaleConnectionCheckEnabled(true).build();// 设置超时时间

				CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
				
				// CloseableHttpClient httpClient = HttpClients.createDefault();  
				HttpHost serverHost = new HttpHost(host, port, "http");
				HttpPost request = new HttpPost();

				try {
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put("channel_name", channel_name);
					paramMap.put("channel_password", channel_password);
					paramMap.put("client_ip", client_ip);
					StringEntity entity = new StringEntity(JSONObject.toJSONString(paramMap));
					request.setEntity(entity);
				} catch (UnsupportedEncodingException e) {
					logger.error(e.getMessage(), e);
				}

				while (true) {

					WAResponseResult result = null;
					CloseableHttpResponse response = null;

					try {
						response = httpClient.execute(serverHost, request);
						HttpEntity respEntity = response.getEntity();
						if (respEntity != null) {
							String msg = EntityUtils.toString(respEntity);
							logger.info("receive message :{}", msg);
							result = JSON.parseObject(msg, WAResponseResult.class);
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					} finally {
						try {
							response.close();
						} catch (NullPointerException e) {
							logger.error("connect timeout or there is no active server.host:{},port:{}.", host, port);
						} catch (IOException e) {
							logger.error(e.getMessage(), e);
						}
					} // get result

					if (result == null) {
						try {
							Thread.sleep(1000);
							logger.info("the result is null...");
							continue;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					// 在“接收响应”返回”无数据”（代码“3”）的情况下，等待1秒。
					if (result.getResult().getCode() == 3) {
						try {
							Thread.sleep(1000);
							logger.info("there is no message...");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					// 根据查询条件查询es，实现数据交互
					if (result.getResult().getCode() == 0) {

						String content = result.getContent();
						String[] str = content.split("_");
						byte[] bytes = Base64.decodeBase64(str[str.length - 1]);
						String xmlContent = new String(bytes, Charsets.UTF_8);

						Map<String, Object> map = WAXmlParseUtil.parse(xmlContent);

						String opCode = (String) map.get(WAXmlParseUtil.OPCODE);
						String msgId = (String) map.get(WAXmlParseUtil.MSGID);
						String opId = (String) map.get(WAXmlParseUtil.OPID);
						String type = (String) map.get(WAXmlParseUtil.TYPE);
						@SuppressWarnings("unchecked")
						Map<String, String> criteria = (Map<String, String>) map.get(WAXmlParseUtil.CRITERIA);
						
						if (opCode.equals(Constant.QUERY_NODE_STATUS) || opCode.equals(Constant.COMM_CONTENT)) {
							logger.info("get wa query staus request,opCode:{},msgId:{},opId:{},criteria:{}",
									opCode, msgId, opId, type, criteria);
							connectResponse(opCode, msgId, opId);
							continue;
						}

						logger.info("get wa send msg,content parse result,opCode:{},msgId:{},opId:{},type:{},criteria:{}",
								opCode, msgId, opId, type, criteria);

						if (type.equals(Constant.QUERY_TYPE)) {
							queryService.query(opCode, msgId, criteria);
						}

						if (type.equals(Constant.MONITOR_TYPE)) {
							monitorService.monitor(opCode, criteria, msgId, opId);
						}

						if (type.equals(Constant.MANAGEMENT_TYPE)) {
							manageService.manage(opCode, criteria, msgId, opId);
						}

					}

				}

			}

		});

		thread.start();
	}

	private void connectResponse(String opCode, String msgId,String opId){
		try {
			List<Data> status = DataUtil.getQueryNodeStatusResonseData();
			String dir = FileUtil.getQueryXMLDir(opCode, Constant.WA_RESPONSE, msgId);
			ResponseInfo response = WaConverter.convertToCommonResponse(status, null, Constant.QUERY_NODE_STATUS, msgId, opId);
			OxmUtil.toXML(response.getResponse(), new File(dir));
			
			SFTPUploadUtil.uploadFile(SFTPUploadUtil.JINGKOU, dir);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
	}
	
	public static void main(String[] args) {
		
		RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(30000)
				.setConnectTimeout(30000).setConnectionRequestTimeout(30000)
				.setStaleConnectionCheckEnabled(true).build();// 设置超时时间

		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
		
		// CloseableHttpClient httpClient = HttpClients.createDefault();  
		HttpHost serverHost = new HttpHost("220.248.98.69", 8081, "http");
		HttpPost request = new HttpPost();
		
		

		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("channel_name", 74);
			paramMap.put("channel_password", "Ximalaya12#$");
			paramMap.put("client_ip", "211.144.203.114,124.74.245.250,114.80.170.73,114.80.143.1-114.80.143.31 255.255.255.224");
			StringEntity entity = new StringEntity(JSONObject.toJSONString(paramMap));
			request.setEntity(entity);
			CloseableHttpResponse response = httpClient.execute(serverHost, request);
			HttpEntity respEntity = response.getEntity();
			if (respEntity != null) {
				String msg = EntityUtils.toString(respEntity);
				System.out.println(msg);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
//		try {
//			List<Data> status = DataUtil.getQueryNodeStatusResonseData();
//			String dir = FileUtil.getQueryXMLDir(Constant.QUERY_NODE_STATUS, Constant.WA_RESPONSE, "0100001502420266020114");
//			ResponseInfo response = WaConverter.convertToCommonResponse(status, null, Constant.QUERY_NODE_STATUS, "0100001502420266020114", "1111111");
//			OxmUtil.toXML(response.getResponse(), new File(dir));
//			
////			SFTPUploadUtil.uploadFile(SFTPUploadUtil.JINGKOU, dir);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
}
