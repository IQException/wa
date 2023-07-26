package com.ximalaya.wa.monitor.model;

import java.util.Date;

import org.apache.hadoop.yarn.webapp.hamlet.HamletSpec.Element;

public class Condition {

	private String opcode;
	private String conditionKey;
	private String conditionValue;
	private String pmId;
	private Date   createAt;
	
	public String getOpcode() {
		return opcode;
	}
	public void setOpcode(String opcode) {
		this.opcode = opcode;
	}
	public String getConditionKey() {
		return conditionKey;
	}
	public void setConditionKey(String conditionKey) {
		this.conditionKey = conditionKey;
	}
	public String getConditionValue() {
		return conditionValue;
	}
	public void setConditionValue(String conditionValue) {
		this.conditionValue = conditionValue;
	}
	public String getPmId() {
		return pmId;
	}
	public void setPmId(String pmId) {
		this.pmId = pmId;
	}
	public Date getCreateAt() {
		return createAt;
	}
	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	
	
}
