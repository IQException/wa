package com.ximalaya.wa.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.ximalaya.wa.service.IWAMonitorService;

@Controller
public class MonitorController {
	
	private final Logger logger = LoggerFactory.getLogger(MonitorController.class);

	@Autowired
	private IWAMonitorService monitorService;
	
	private RSA rsa = new RSA(RSA.getPrivateKey());
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/monitor")
	@ResponseBody
	public String monitor(@RequestParam String opCode,String param){
		
		logger.info("param => {}");
		
		String data = rsa.decryptByPrivateKey(param);
		
		try {
			Map<String, String> criteria = JSON.parseObject(data, Map.class);
			monitorService.monitor(opCode, criteria, "3101161522734412068", "11531000020077");
		} catch (Exception e) {
			return "{opCode:\""+ opCode + "\",data:\"" + data + "\",message:\"" +  e.getMessage() + "\"}";
		}
		
		return "{opCode:\""+ opCode + "\",data:\"" + data + "\",message:\"SUCCESS\"}";
	}
	
}
