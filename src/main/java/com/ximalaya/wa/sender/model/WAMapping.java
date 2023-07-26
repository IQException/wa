package com.ximalaya.wa.sender.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class WAMapping {

	private Map<String, String> indexMap        = new HashMap<>();
	private Map<String, String> dataMap         = new HashMap<>();
	private List<String>        noFieldDataList = new ArrayList<>();
	
	/**
	 * data 分为三种类型
	 * 一种是确定表里面没有的字段。
	 * 一种是表里面有的字段。
	 * 一种是需要转化的字段。 => 专门的转换方法
	 * 还有可能是其他的字段。
	 */

	@PostConstruct
	public void init() {
		
		/**
		 * opcode和index的映射
		 */
		Map<String, String> indexMap = new HashMap<>();
		indexMap.put("QUERYACCOUNT",             "event_user_profile");                        // 用户注册信息
		indexMap.put("QUERYRELATIONACCOUNTINFO", "event_third_account_relation*");             // 注册-关联账号信息
		indexMap.put("QUERYLOG",                 "event_login*");                              // 登录日志信息
		indexMap.put("QUERYMEDIABROWSELOG",      "event_play*");                               // 播放音视频/浏览图片日志信息
		indexMap.put("QUERYCOMMENT",             "event_comment*");                            // 评论信息
		indexMap.put("QUERYBARRAGE",             "event_comment*");                            // 弹幕信息
		indexMap.put("QUERYRECHARGEWITHDRAWALS", "event_paid_recharge*");                      // 充值信息
		indexMap.put("QUERYREWARD",              "event_reward*");                             // 赠送/打赏信息
		indexMap.put("QUERYPAYMENT",             "event_paid_payment*");                       // 支付信息
		// indexMap.put(Constant.QUERY_PAYMENT_ORDER, paymentOrderIndex);					   // 下单信息
		this.setIndexMap(indexMap);
		
		
		/**
		 * es 表中没有的字段
		 */
		noFieldDataList.add("HARDWARESTRING");        // 硬件特征串
		noFieldDataList.add("IMSI");             	  // IMSI号
		noFieldDataList.add("SERIALNUM");             // 充值交易流水号❓
		noFieldDataList.add("FILEURL");               // 文件url❓

		
		/**
		 * condition和esfield的映射
		 */
		Map<String, String> dataMap = new HashMap<>();
		dataMap.put("ACCOUNTNAME",      "common.uid");                      // 用户帐号
		dataMap.put("USERID",           "common.uid");                      // 用户内部id
		dataMap.put("IP",               "common.ip");             
		dataMap.put("ORDERIP",          "common.ip");             
		dataMap.put("MAC",              "common.macAddress");
		dataMap.put("ORDERMAC",         "common.macAddress");
		dataMap.put("IMEI",             "common.imei"); 
		dataMap.put("USER_INTENRALID",  "common.uid"); 
		dataMap.put("MEDIA_ID",         "data.trackId");                    // 声音id
		dataMap.put("CONTENT",          "data.content");                    
		dataMap.put("REWARD_USERID",    "common.uid"); 
		dataMap.put("RECEIVING_USERID", "data.receiverUid");         
		dataMap.put("GOODS_NAME" ,      "data.giftName");                   // 物品名称
		dataMap.put("ORDERNUM" ,        "data.unified_order_no");           // 订单号
		this.setDataMap(dataMap);

	}

	public Map<String, String> getIndexMap() {
		return indexMap;
	}

	public void setIndexMap(Map<String, String> indexMap) {
		this.indexMap = indexMap;
	}

	public Map<String, String> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, String> dataMap) {
		this.dataMap = dataMap;
	}

	public List<String> getNoFieldDataList() {
		return noFieldDataList;
	}

	public void setNoFieldDataList(List<String> noFieldDataList) {
		this.noFieldDataList = noFieldDataList;
	}
	
}
