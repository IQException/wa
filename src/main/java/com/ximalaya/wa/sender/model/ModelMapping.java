package com.ximalaya.wa.sender.model;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class ModelMapping {

	private Map<String,String> modelMap = new HashMap<>(); 
	
	@PostConstruct
	public void init() {
		Map<String,String> modelMap = new HashMap<>();
		modelMap.put(Constant.QUERY_ACCOUNT,               "com.ximalaya.wa.model.Account");
		modelMap.put(Constant.QUERY_RELATION_ACCOUNT_INFO, "com.ximalaya.wa.model.RelationAccount");
		modelMap.put(Constant.QUERY_UCFORUM_LOG,           "com.ximalaya.wa.model.Login");
		modelMap.put(Constant.QUERY_MEDIA_BROWSE_LOG,      "com.ximalaya.wa.model.Play");
		modelMap.put(Constant.QUERY_COMMENT,               "com.ximalaya.wa.model.Comment");
		modelMap.put(Constant.QUERY_QUERYBARRAGE,          "com.ximalaya.wa.model.Bullet");
		modelMap.put(Constant.QUERY_RECHARGE_WITH_DRAWALS, "com.ximalaya.wa.model.Recharge");
		modelMap.put(Constant.QUERY_REWARD,                "com.ximalaya.wa.model.Donate");
		modelMap.put(Constant.QUERY_PAYMENT,               "com.ximalaya.wa.model.Payment");
		modelMap.put(Constant.QUERY_PAYMENT_ORDER,         "com.ximalaya.wa.model.PaymentOrder");
		this.setModelMap(modelMap);
	}

	public Map<String, String> getModelMap() {
		return modelMap;
	}

	public void setModelMap(Map<String, String> modelMap) {
		this.modelMap = modelMap;
	}
	
}
