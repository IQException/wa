package com.ximalaya.wa.model;

import java.math.RoundingMode;
import java.text.NumberFormat;

import org.apache.commons.lang.StringUtils;

import com.nali.common.util.StringUtil;
import com.ximalaya.wa.annotation.Mapped;
import com.ximalaya.wa.cache.GuavaCache;
import com.ximalaya.wa.converter.WaConverter;

public abstract class BaseModel implements CommonProcessor {

	protected static NumberFormat nf = NumberFormat.getInstance();
	static {
		nf.setMaximumFractionDigits(6);
		nf.setRoundingMode(RoundingMode.DOWN);
	}

	@Mapped(wa = "ACTION_TIME", xm = "common.serverTime,data.created_at")
	private String actionTime;
	@Mapped(wa = "IP", xm = "common.ip")
	private String ip;
	@Mapped(wa = "PORT", xm = "common.clientPort")
	private String port;
	@Mapped(wa = "HARDWARESTRING", xm = "")
	private String deviceId;
	@Mapped(wa = "MAC", xm = "common.macAddress")
	private String macAddress;
	@Mapped(wa = "IMEI", xm = "common.imei")
	private String imei;
	@Mapped(wa = "LONGITUDE", xm = "common.longitude")
	private String longitude;
	@Mapped(wa = "LATITURE", xm = "common.latitude")
	private String latitude;
	@Mapped(wa = "CITY_AREACODE", xm = "common.areaCode,data.registerAreaCode")
	private String areacode;
	@Mapped(wa = "USER_INTENRALID", xm = "common.uid,data.uid" )
	private String uid;
	@Mapped(wa = "USER_ACCOUNT", xm = "common.uid",enc=true)
	private String account;
	
	//monitor result 自定义字段
	private String pmId;
	
	public String getPmId() {
		return pmId;
	}

	public void setPmId(String pmId) {
		this.pmId = pmId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getActionTime() {
		return actionTime;
	}

	public void setActionTime(String actionTime) {
		this.actionTime = actionTime;
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

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
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

	public String getAreacode() {
		return areacode;
	}

	public void setAreacode(String areacode) {
		this.areacode = areacode;
	}

	public void fixInfo() {
		
		setActionTime( StringUtils.isBlank(getActionTime()) ? null :getActionTime().substring(0, 10));
		setMacAddress(StringUtils.isBlank(getMacAddress()) ? null :StringUtil.subwidth(WaConverter.escapePunct(getMacAddress()), 12) );
		setLatitude(StringUtils.isBlank(getLatitude()) ? null : nf.format(Double.parseDouble(getLatitude())));
		setLongitude(StringUtils.isBlank(getLongitude())  ? null : nf.format(Double.parseDouble(getLongitude())));
		setAccount(StringUtils.isBlank(getUid()) ? null : GuavaCache.getAccountByUid(getUid()));
		setAreacode(StringUtils.isBlank(getAreacode()) ? null : transferAreaCode(getAreacode()));
		setIp(WaConverter.ip2Long(getIp()));

	}
	protected String transferAreaCode(String areaCode){
		String lastFourChar4areaCode = areaCode.substring(areaCode.length() - 4);
		return lastFourChar4areaCode + "00";
	}

}
