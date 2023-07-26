package com.ximalaya.wa.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nali.common.util.StringUtil;
import com.ximalaya.wa.annotation.Mapped;
import com.ximalaya.wa.assembler.DataAssembler;
import com.ximalaya.wa.cache.GuavaCache;
import com.ximalaya.wa.cache.RedisCache;
import com.ximalaya.wa.config.Dict;
import com.ximalaya.wa.converter.WaConverter;
import com.ximalaya.wa.model.xml.Item;

import scala.Option;
import scala.Tuple2;

public class Filter extends  BaseModel{

	private static final Logger LOG = LoggerFactory.getLogger(Filter.class);
	@Mapped(wa = "ACTION_TIME", xm = "updated_at")
	private String actionTime;
	@Mapped(wa = "IP", xm = "ip")
	private String ip;
	@Mapped(wa = "PORT", xm = "")
	private String port;
	@Mapped(wa = "HARDWARESTRING", xm = "")
	private String deviceId;
	@Mapped(wa = "MAC", xm = "mac")
	private String macAddress;
	@Mapped(wa = "IMEI", xm = "")
	private String imei;
	@Mapped(wa = "LONGITUDE", xm = "longitude")
	private String longitude;
	@Mapped(wa = "LATITURE", xm = "latitude")
	private String latitude;
	@Mapped(wa = "CITY_AREACODE", xm = "")
	private String areacode;
	@Mapped(wa = "USER_INTENRALID", xm = "uid" )
	private String uid;
	@Mapped(wa = "USER_ACCOUNT", xm = "uid",enc=true)
	private String account;	
	@Mapped(wa = "FILE_ID", xm = "id")
	private String trackId;
	@Mapped(wa = "FILE_NAME", xm = "title")
	private String trackTitle;
	@Mapped(wa = "FILE_TYPE", xm = "")
	private String fileType;
	@Mapped(wa = "FILE_DESCRIBE", xm = "intro")
	private String trackIntro;
	@Mapped(wa = "FILE_FORM", xm = "")
	private String trackFormat;
	@Mapped(wa = "FILE_SIZE", xm = "")
	private String size;
	@Mapped(wa = "FILE_MD5", xm = "")
	private String fileMd5;
	@Mapped(wa = "FILE_ CONTENT", xm = "")
	private String content;
	@Mapped(wa = "FILE_URL", xm = "")
	private String url;
	@Mapped(wa = "TRIGGER_TYPE", xm = "")
	private String triggerType;
	@Mapped(wa = "TRIGGER_EIGENVALUE", xm = "offlineReason")
	private String triggerContent;
	@Mapped(wa = "ACTION_TYPE", xm = "")
	private String actionType;
	@Mapped(wa = "VIOLATION_TYPE", xm = "ban_type")
	private String violationType;
	
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
		
        return this.areacode;
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
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	public String getTrackTitle() {
		return trackTitle;
	}
	public void setTrackTitle(String trackTitle) {
		this.trackTitle = trackTitle;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getTrackIntro() {
		return trackIntro;
	}
	public void setTrackIntro(String trackIntro) {
		this.trackIntro = trackIntro;
	}
	public String getTrackFormat() {
		return trackFormat;
	}
	public void setTrackFormat(String trackFormat) {
		this.trackFormat = trackFormat;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getFileMd5() {
		return fileMd5;
	}
	public void setFileMd5(String fileMd5) {
		this.fileMd5 = fileMd5;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTriggerType() {
		return triggerType;
	}
	public void setTriggerType(String triggerType) {
		this.triggerType = triggerType;
	}
	public String getTriggerContent() {
		return triggerContent;
	}
	public void setTriggerContent(String triggerContent) {
		this.triggerContent = triggerContent;
	}
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	public String getViolationType() {
		return violationType;
	}
	public void setViolationType(String violationType) {
		this.violationType = violationType;
	}
	@Override
	public void fixInfo()  {

		setUrl(DataAssembler.URL_PREFIX+getTrackId());
	    setActionType(Dict.ACTION_DELETE);
	    setTriggerType(Dict.TRIGGER_MD5);
		setViolationType(transferViolationType(getViolationType()));
		
		setMacAddress(StringUtils.isBlank(getMacAddress()) ? null :StringUtil.subwidth(WaConverter.escapePunct(getMacAddress()), 12) );
		setLatitude(StringUtils.isBlank(getLatitude()) ? null : nf.format(Double.parseDouble(getLatitude())));
		setLongitude(StringUtils.isBlank(getLongitude())  ? null : nf.format(Double.parseDouble(getLongitude())));
		setAccount(StringUtils.isBlank(getUid()) ? null : GuavaCache.getAccountByUid(getUid()));
		try {
			setActionTime( StringUtils.isBlank(getActionTime()) ? null :String.valueOf(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+08:00'").parse(getActionTime()).getTime()).substring(0,10));
		} catch (ParseException e) {
		    e.printStackTrace();
		}
		setFileMd5(StringUtils.isBlank(getTrackId()) ? null :RedisCache.getMd5ByTrackId(getTrackId()));
		setAreacode();
		setIp(WaConverter.ip2Long(getIp()));

	}
	
	private void setAreacode() {
		if(StringUtils.isBlank(getIp())) return ;
		Tuple2<Option<Object>, Option<Object>> _areaCode = com.ximalaya.data.common.util.dimension.GeoUtils.getLocationByIp(getIp());

		if( _areaCode._2.isDefined()) {	
	        	setAreacode(_areaCode._2.get()+"00");	        
		}
	}
	private String transferViolationType(String type){
		
		if("7".equals(type)){
			return Dict.VIOLATION_POLITICS;
		}else if("15".equals(type)){
			return Dict.VIOLATION_TERRORISM;
		}else if("13".equals(type)){
			return Dict.VILOATION_EROTICISM;
		}else{
			return Dict.VIOLATION_OTHER;
		}
	}
	
	public static Item getOpCode() {
		return DataAssembler.getOpCode(Dict.OP_FILTER);
	}

}
