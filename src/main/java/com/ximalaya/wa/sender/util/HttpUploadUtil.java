package com.ximalaya.wa.sender.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUploadUtil {

	private static Logger		logger	= LoggerFactory.getLogger(HttpUploadUtil.class);

	private static final String	URL		= "";

	public static void upload(String filePath) {
		
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		
		try {
			httpClient = HttpClients.createDefault();

			HttpPost httpPost = new HttpPost(URL);

			// 把文件转换成流对象FileBody
			FileBody bin = new FileBody(new File(filePath));

			HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("file", bin).build();

			httpPost.setEntity(reqEntity);

			// 发起请求 并返回请求的响应
			response = httpClient.execute(httpPost);

			logger.info("The response value of token: {}",response.getFirstHeader("token"));
			
			// 获取响应对象
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				// 打印响应内容
				logger.info("response content : {}",EntityUtils.toString(resEntity, Charset.forName("UTF-8")));
			}

			EntityUtils.consume(resEntity);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				if (httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
