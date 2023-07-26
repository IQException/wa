package com.ximalaya.wa.model;

import org.apache.commons.lang.StringUtils;

import com.ximalaya.wa.annotation.Mapped;
import com.ximalaya.wa.assembler.DataAssembler;
import com.ximalaya.wa.cache.GuavaCache;
import com.ximalaya.wa.config.Dict;
import com.ximalaya.wa.model.xml.Item;

public class Donate  extends BaseModel {
	@Mapped(wa = "RECEIVER_INTENRALID", xm = "data.receiverUid")
	private String recUid;
	@Mapped(wa = "RECEIVER_ACOCUNT", xm = "")
	private String recAccount;
	@Mapped(wa = "GOODS_NAME", xm = "data.giftName",enc=true)
	private String goodsName;
	@Mapped(wa = "GOODS_COUNT", xm = "data.quantity")
	private String goodsCount;
	@Mapped(wa = "GOODS_AMOUNT", xm = "data.amount")
	private String amount;
	@Mapped(wa = "CURRENCYUNIT", xm = "")
	private String currency ;
	@Mapped(wa = "ACTION_TYPE", xm = "")
	private String actionType;
	
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	public String getRecUid() {
		return recUid;
	}
	public void setRecUid(String recUid) {
		this.recUid = recUid;
	}
	public String getRecAccount() {
		return recAccount;
	}
	public void setRecAccount(String recAccount) {
		this.recAccount = recAccount;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public String getGoodsCount() {
		return goodsCount;
	}
	public void setGoodsCount(String goodsCount) {
		this.goodsCount = goodsCount;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public static Item getOpCode() {
		return DataAssembler.getOpCode(Dict.OP_DONATE);
	}
	
	public static Item getQueryOpCode() {
		return DataAssembler.getOpCode(Dict.OP_QUERY_DONATE);
	}
	
	@Override
	public void fixInfo() {
		super.fixInfo();
		setCurrency("喜点");		
		setRecAccount(StringUtils.isBlank(getRecUid()) ? null : GuavaCache.getAccountByUid(getRecUid()));
		setActionType(Dict.ACTION_GIFT);
	}
}
