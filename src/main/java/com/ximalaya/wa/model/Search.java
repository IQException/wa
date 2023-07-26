package com.ximalaya.wa.model;

import java.text.NumberFormat;

import com.ximalaya.wa.annotation.Mapped;
import com.ximalaya.wa.assembler.DataAssembler;
import com.ximalaya.wa.config.Dict;
import com.ximalaya.wa.model.xml.Item;

public class Search extends BaseModel {




	@Mapped(wa = "SEARCH_CONTENT", xm = "data.kw",enc=true)
	private String content;
	@Mapped(wa = "BOARD", xm = "data.core")
	private String module;
	@Mapped(wa = "MEDIA_TYPE", xm = "")
	private String sampleType;
	@Mapped(wa = "SEARCHOBJECT_TYPE", xm = "")
	private String targetType;
	@Mapped(wa = "MOBILEPHONE", xm = "")
	private String phoneNum;
	@Mapped(wa = "KEYWORD", xm = "data.kw",enc=true)
	private String keyWord;
	@Mapped(wa = "SEARCH_ID", xm = "")
	private String searchId;
	@Mapped(wa = "LATITUDE", xm = "common.latitude")
	private String latitude;
	@Mapped(wa = "URL", xm = "")
	private String url;
	@Mapped(wa = "IP_ADDRESS", xm = "common.ip")
	private String ip;
	
	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getSampleType() {
		return sampleType;
	}

	public void setSampleType(String sampleType) {
		this.sampleType = sampleType;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public static NumberFormat getNf() {
		return nf;
	}

	public static void setNf(NumberFormat nf) {
		Search.nf = nf;
	}

	public String getSearchId() {
		return searchId;
	}

	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public static Item getOpCode() {
		return DataAssembler.getOpCode(Dict.OP_SEARCH);
	}
	
	@Override
	public void fixInfo() {


	    super.fixInfo();
		setSampleType(Dict.MEDIA_MESSAGE_TEXT);
	}
}
