package com.ximalaya.wa.manage.model;

import java.util.Date;

public class WaManage {

	private Long	id;
	private String	manageKey;
	private String	manageValue;
	private Long	startTime;
	private Long	endTime;
	private String	paramKey;
	private String  data;
	private Date	createAt;
	private int     status = 0;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getManageKey() {
		return manageKey;
	}
	public void setManageKey(String manageKey) {
		this.manageKey = manageKey;
	}
	public String getManageValue() {
		return manageValue;
	}
	public void setManageValue(String manageValue) {
		this.manageValue = manageValue;
	}
	public Long getStartTime() {
		return startTime;
	}
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	public Long getEndTime() {
		return endTime;
	}
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
	public String getParamKey() {
		return paramKey;
	}
	public void setParamKey(String paramKey) {
		this.paramKey = paramKey;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public Date getCreateAt() {
		return createAt;
	}
	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
