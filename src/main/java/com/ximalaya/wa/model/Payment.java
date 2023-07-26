package com.ximalaya.wa.model;

import org.apache.commons.lang.StringUtils;

import com.ximalaya.wa.annotation.Mapped;
import com.ximalaya.wa.assembler.DataAssembler;
import com.ximalaya.wa.config.Dict;
import com.ximalaya.wa.model.xml.Item;
import com.ximalaya.wa.sender.model.ChannelType;

public class Payment extends BaseModel{
	
	@Mapped(wa = "ORDER_ID", xm = "data.unified_order_no")
	private String orderId;
	@Mapped(wa = "SERIAL_NUM", xm = "")
	private String serialNum;
	@Mapped(wa = "GOODS_NAME", xm = "",enc=true)  // 要查es
	private String goodsName;
	@Mapped(wa = "PAY_STATUS", xm = "data.pay_status")
	private String payStatus;
	@Mapped(wa = "PAY_MONEY", xm = "data.amount")
	private String payMoney;
	@Mapped(wa = "PAY_TYPE", xm = "data.channel_type_id")//要转一下
	private String payType;
	@Mapped(wa = "PAY_USERID", xm = "data.payer_id")
	private String payUserId;
	@Mapped(wa = "PAY_USER_TYPE", xm = "")
	private String payUserType;
	@Mapped(wa = "BANK_NAME", xm = "")
	private String bankName;
	@Mapped(wa = "AMOUNT", xm = "")
	private String amount;
	@Mapped(wa = "REMARK", xm = "",enc=true)
	private String remark;
	@Mapped(wa = "RECEIVER_ID", xm = " data.payee_id")
	private String receiverId;
	@Mapped(wa = "RECEIVER_ACCOUNT", xm = "")
	private String receiverAccount;
	@Mapped(wa = "RECEIVER_TYPE", xm = "")
	private String receiverType;
	@Mapped(wa = "IMSI", xm = "")
	private String imsi;
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getSerialNum() {
		return serialNum;
	}
	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public String getPayStatus() {
		return payStatus;
	}
	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}
	public String getPayMoney() {
		return payMoney;
	}
	public void setPayMoney(String payMoney) {
		this.payMoney = payMoney;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getPayUserId() {
		return payUserId;
	}
	public void setPayUserId(String payUserId) {
		this.payUserId = payUserId;
	}
	public String getPayUserType() {
		return payUserType;
	}
	public void setPayUserType(String payUserType) {
		this.payUserType = payUserType;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getReceiverAccount() {
		return receiverAccount;
	}
	public void setReceiverAccount(String receiverAccount) {
		this.receiverAccount = receiverAccount;
	}
	public String getReceiverType() {
		return receiverType;
	}
	public void setReceiverType(String receiverType) {
		this.receiverType = receiverType;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	
	public static Item getOpCode() {
		return DataAssembler.getOpCode(Dict.OP_PAYMENT);
	}
	public static Item getQueryOpCode(){
		return DataAssembler.getOpCode(Dict.OP_QUERY_PAYMENT);
	}
	public static Item getMonitorOpCode() {
		return DataAssembler.getOpCode(Dict.OP_MONITOR_PAYMENT_ADD);
	}
	
	@Override
	public void fixInfo(){
		super.fixInfo();
		setPayType(StringUtils.isBlank(this.getPayType()) ? null : ChannelType.getPaymentModelByChannelIdStr(this.getPayType()));
	}
	
}
