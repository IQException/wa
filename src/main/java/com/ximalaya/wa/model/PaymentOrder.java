package com.ximalaya.wa.model;

import com.ximalaya.wa.annotation.Mapped;
import com.ximalaya.wa.assembler.DataAssembler;
import com.ximalaya.wa.config.Dict;
import com.ximalaya.wa.model.xml.Item;

public class PaymentOrder extends BaseModel{
	
	@Mapped(wa = "USER_TYPE", xm = "data.user_type")
	private String userType;
	@Mapped(wa = "ORDER_ID", xm = "data.unified_order_no")
	private String orderId;
	@Mapped(wa = "GOODS_NAME", xm = "data.goods_name")
	private String goodsName;
	@Mapped(wa = "GOODS_ID", xm = "data.album_id")
	private String goodsId;
	@Mapped(wa = "GOODS_PRICE", xm = "data.goods_price")
	private String goodsPrice;
	@Mapped(wa = "GOODS_COUNT", xm = "data.goods_count")
	private String goodsCount;
	@Mapped(wa = "SUM_MONEY", xm = "data.discount_amount")
	private String sumMoney;
	@Mapped(wa = "ORDER_STATE", xm = "data.order_state")
	private String orderState;
	@Mapped(wa = "ORDER_IP", xm = "common.ip")
	private String ip;
	@Mapped(wa = "ORDER_PORT", xm = "common.clientPort")
	private String port;
	@Mapped(wa = "ORDER_MAC", xm = "common.macAddress")
	private String macAddress;
	@Mapped(wa = "ORDER_HARDWARESTRING", xm = "")
	private String deviceId;
	@Mapped(wa = "ORDER_LONGITUDE", xm = "common.longitude")
	private String longitude;
	@Mapped(wa = "ORDER_LATITURE", xm = "common.latitude")
	private String latitude;
	@Mapped(wa = "ORDER_IMEI", xm = "common.imei")
	private String imei;
	@Mapped(wa = "ORDER_IMSI", xm = "")
	private String imsi;
	
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public String getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	public String getGoodsPrice() {
		return goodsPrice;
	}
	public void setGoodsPrice(String goodsPrice) {
		this.goodsPrice = goodsPrice;
	}
	public String getGoodsCount() {
		return goodsCount;
	}
	public void setGoodsCount(String goodsCount) {
		this.goodsCount = goodsCount;
	}
	public String getSumMoney() {
		return sumMoney;
	}
	public void setSumMoney(String sumMoney) {
		this.sumMoney = sumMoney;
	}
	public String getOrderState() {
		return orderState;
	}
	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getImsi() {
        return imsi;
    }
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }
    
    public static Item getOpCode() {
		return DataAssembler.getOpCode(Dict.OP_PAYMENT_ORDER);
	}
    public static Item getQueryOpCode(){
		return DataAssembler.getOpCode(Dict.OP_QUERY_PAYMENT_ORDER);
	}
   
	public static Item getMonitorOpCode() {
		return DataAssembler.getOpCode(Dict.OP_MONITOR_PAYMENTORDER_ADD);
	}

}
