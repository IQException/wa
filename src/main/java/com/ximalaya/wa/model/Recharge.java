package com.ximalaya.wa.model;

import org.apache.commons.lang.StringUtils;

import com.ximalaya.wa.annotation.Mapped;
import com.ximalaya.wa.assembler.DataAssembler;
import com.ximalaya.wa.config.Dict;
import com.ximalaya.wa.model.xml.Item;
import com.ximalaya.wa.sender.model.PayChannel;

public class Recharge extends BaseModel{
	
	@Mapped(wa = "SERIAL_NUM", xm = "data.unified_order_no")
	private String serialNum;
	@Mapped(wa = "DEAL_ACCOUNTTYPE", xm = "data.pay_channel")
	private String dealAccountType;
	@Mapped(wa = "DEAL_ACCOUNT", xm = "")
	private String dealAccount;
	@Mapped(wa = "DEAL_MONEY", xm = "data.amount")
	private String dealMoney;
	@Mapped(wa = "IMSI", xm = "")
	private String imsi;
	@Mapped(wa = "ACTION_TYPE", xm = "")
	private String actionType;
	
	public String getSerialNum() {
		return serialNum;
	}
	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}
	public String getDealAccountType() {
		return dealAccountType;
	}
	public void setDealAccountType(String dealAccountType) {
		this.dealAccountType = dealAccountType;
	}
	public String getDealAccount() {
		return dealAccount;
	}
	public void setDealAccount(String dealAccount) {
		this.dealAccount = dealAccount;
	}
	public String getDealMoney() {
		return dealMoney;
	}
	public void setDealMoney(String dealMoney) {
		this.dealMoney = dealMoney;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	
	public static Item getOpCode() {
		return DataAssembler.getOpCode(Dict.OP_RECHARGE);
	}
	public static Item getQueryOpCode(){
		return DataAssembler.getOpCode(Dict.OP_QUERY_RECHARGE);
	}
	public static Item getMonitorOpCode() {
		return DataAssembler.getOpCode(Dict.OP_MONITOR_RECHARGE_ADD);
	}
	
	@Override
	public void fixInfo() {
		super.fixInfo();
		setDealAccountType(StringUtils.isBlank(this.getDealAccountType()) ? null : PayChannel.getPaymentModelByPayChannelId(Integer.valueOf(this.getDealAccountType())));
	}

}
