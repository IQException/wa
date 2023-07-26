package com.ximalaya.wa.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.ximalaya.wa.service.IWAQueryService;

@Controller
public class QueryController {
	
	private final Logger logger = LoggerFactory.getLogger(QueryController.class);
	
	@Autowired
	private IWAQueryService queryService;
	
	private RSA rsa = new RSA(RSA.getPrivateKey());

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/query",method = RequestMethod.GET)
	@ResponseBody
	private String query(@RequestParam String opCode,String param) throws UnsupportedEncodingException{
		
		logger.info("param => {}",param);
		
		String data = rsa.decryptByPrivateKey(param);
		logger.info("query data => {}",data);
		
		try {
			
			Map<String, String> map = JSON.parseObject(data, Map.class);
			
			queryService.query(opCode, "3101161522734412068", map);
		} catch (Exception e) {
			return "{opCode:\""+ opCode + "\",data:\"" + data + "\",message:\"" +  e.getMessage() + "\"}";
		}
		return "{opCode:\""+ opCode + "\",data:\"" + data + "\",message:\"SUCCESS\"}";
	}
	
	
	
}
