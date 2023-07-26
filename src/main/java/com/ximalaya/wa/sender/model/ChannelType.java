package com.ximalaya.wa.sender.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hadoop.hbase.util.CollectionBackedScanner;
import org.apache.spark.util.CollectionsUtils;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;

public enum ChannelType {

	ANDROID_APP_ALIPAY(1,"06"),
    ANDROID_APP_WEIXIN(2,"06"),
    APPLE_IAP(3,"06"),
    BACKGATE_APPLE(4,"06"),
    BACKGATE_ANDROID(5,"06"),
    APPLE_JS_WEIXIN(6,"06"),
    ANDROID_JS_WEIXIN(7,"06"),
    ANDROID_XI_COIN(8,"03"),
    APPLE_XI_COIN(9,"03"),
    APPLE_REWARD(10,"06"),
    ANDROID_REWARD(11,"06"),
    ANDROID_XI_PAY(12,"06"),
    APPLE_XI_PAY(13,"06"),
    APPLE_JS_ALIPAY(14,"06"),
    ANDROID_JS_ALIPAY(15,"06"),
    APPLE_REDEEM(16,"06"),
    ANDROID_REDEEM(17,"06"),
    APPLE_NATIVE_WEIXIN(18,"06"),
    ANDROID_NATIVE_WEIXIN(19,"06"),
    APPLE_DIRECT(20,"06"),
    ANDROID_DIRECT(21,"06"),
    ANDROID_RECHARGE_BACK(22,"06"),   //android recharge back xidian
    APPLE_RECHARGE_BACK(23,"06"),

    ANDROID_OPEN_ALIPAY(24,"06"),
    APPLE_OPEN_ALIPAY(25,"06"),
    ANDROID_OPEN_WEIXIN(26,"06"),
    APPLE_OPEN_WEIXIN(27,"06"),
    APPLE_OPEN_IAP(28,"06"),
    ANDROID_OPEN_BAIDU_WALLET(29,"06"),
    APPLE_OPEN_BAIDU_WALLET(30,"06"),

    APPLE_COUPON(31,"06"),
    ANDROID_COUPON(32,"06"),

    APPLE_VIP(33,"06"),
    ANDROID_VIP(34,"06");

    private int id;
    private String paymentModel;

    ChannelType(int id,String paymentModel) {
        this.id = id;
        this.paymentModel = paymentModel;
    }

    public int getId() {
        return id;
    }
    
    public String getPaymentModel(){
    	return paymentModel;
    }

    public static String getPaymentModelByChannelId(int id){
    	
    	for (ChannelType channelType : ChannelType.values()) {
			if (channelType.getId() == id) {
				return channelType.getPaymentModel();
			}
		}
    	
    	return null;
    }
    
    public static String getPaymentModelByChannelIdStr(String str){
    	
    	List<String> ids = new ArrayList<>();
    	
    	List<String> idStrs = JSON.parseArray(str, String.class);
    	
    	for (String idStr : idStrs) {
			Integer id = Integer.valueOf(idStr);
			String paymentModel = getPaymentModelByChannelId(id);
			if (paymentModel != null && !ids.contains(paymentModel)) {
				ids.add(paymentModel);
			}
		}
    	
    	if (!CollectionUtils.isEmpty(ids)) {
    		return JSON.toJSONString(ids);
		}

    	return null;
    	
    }
    
    public static void main(String[] args) {
//		System.out.println(getPaymentModelByChannelId(13));
//		System.out.println(getPaymentModelByChannelId(8));
//		System.out.println(getPaymentModelByChannelId(9));
//		System.out.println(getPaymentModelByChannelId(35));
		
		System.out.println(getPaymentModelByChannelIdStr("[8]"));
		System.out.println(getPaymentModelByChannelIdStr("[0]"));
		System.out.println(getPaymentModelByChannelIdStr("[5,6,8]"));
	}
	
}
