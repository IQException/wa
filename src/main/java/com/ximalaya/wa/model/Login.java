package com.ximalaya.wa.model;

import org.apache.commons.lang.StringUtils;

import com.ximalaya.wa.annotation.Mapped;
import com.ximalaya.wa.assembler.DataAssembler;
import com.ximalaya.wa.config.Dict;
import com.ximalaya.wa.model.xml.Item;

public class Login extends  BaseModel  {

	@Mapped(wa = "ACTION_TIME", xm = "common.serverTime")
	private String actionTime;
	@Mapped(wa = "LOGIN_IP", xm = "common.ip")
	private String ip;
	@Mapped(wa = "LOGIN_PORT", xm = "common.clientPort")
	private String port;
	@Mapped(wa = "LOGIN_HARDWARESTRING", xm = "")
	private String deviceId;
	@Mapped(wa = "LOGIN_MAC", xm = "common.macAddress")
	private String macAddress;
	@Mapped(wa = "LOGIN_IMEI", xm = "common.imei")
	private String imei;
	@Mapped(wa = "LOGIN_LONGITUDE", xm = "common.longitude")
	private String longitude;
	@Mapped(wa = "LOGIN_LATITUDE", xm = "common.latitude")
	private String latitude;
	@Mapped(wa = "CITY_AREACODE", xm = "common.areaCode")
	private String areacode;
	@Mapped(wa = "USER_INTENRALID", xm = "common.uid" )
	private String uid;
	@Mapped(wa = "USER_ACCOUNT", xm = "common.uid",enc=true)
	private String account;
	@Mapped(wa = "USER_TYPE", xm = "")
	private String userType;
	@Mapped(wa = "LOGIN_SOURCE", xm = "")
	private String loginSource;
	@Mapped(wa = "LOGIN_IMSI", xm = "")
	private String imsi;
	@Mapped(wa = "BASE_STATION_ID", xm = "")
	private String baseStationId;
	@Mapped(wa = "BASE_STATION_POSITION", xm = "")
	private String baseStationPosition;
	@Mapped(wa = "TERMINAL_TYPE", xm = "common.deviceName")
	private String termType;
	@Mapped(wa = "TERMINAL_ID", xm = "common.deviceType")
	private String termId;
	@Mapped(wa = "TERMINAL_OS", xm = "common.deviceName")
	private String os;
	@Mapped(wa = "TERMINAL_OS_VERSION", xm = "common.os")
	private String osVersion;
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
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getLoginSource() {
		return loginSource;
	}
	public void setLoginSource(String loginSource) {
		this.loginSource = loginSource;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public String getBaseStationId() {
		return baseStationId;
	}
	public void setBaseStationId(String baseStationId) {
		this.baseStationId = baseStationId;
	}
	public String getBaseStationPosition() {
		return baseStationPosition;
	}
	public void setBaseStationPosition(String baseStationPosition) {
		this.baseStationPosition = baseStationPosition;
	}
	public String getTermType() {
		return termType;
	}
	public void setTermType(String termType) {
		this.termType = termType;
	}
	public String getTermId() {
		return termId;
	}
	public void setTermId(String termId) {
		this.termId = termId;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String getOsVersion() {
		return osVersion;
	}
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}
	public static Item getOpCode() {
		return DataAssembler.getOpCode(Dict.OP_LOGIN);
	}
	public static Item getQueryOpCode() {
		return DataAssembler.getOpCode(Dict.OP_QUERY_LOGIN);
	}
	@Override
	public void fixInfo() {

	    super.fixInfo();

		setUserType(StringUtils.isBlank(getUid()) ? Dict.USERTYPE_VISITOR:Dict.USERTYPE_USER );
		
	}
	
}
