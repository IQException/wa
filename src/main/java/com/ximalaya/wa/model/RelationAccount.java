package com.ximalaya.wa.model;

import org.apache.commons.lang.StringUtils;

import com.ximalaya.wa.annotation.Mapped;
import com.ximalaya.wa.assembler.DataAssembler;
import com.ximalaya.wa.cache.GuavaCache;
import com.ximalaya.wa.config.Dict;
import com.ximalaya.wa.model.xml.Item;

public class RelationAccount implements CommonProcessor {

	@Mapped(wa = "RELATIONACCOUNT_TYPE", xm = "")
	private String relatedAccountType;
	@Mapped(wa = "RELATIONACCOUNT_INTENRALID", xm = "")
	private String relatedAccountUid;
	@Mapped(wa = "RELATIONACCOUNT", xm = "")
	private String relatedAccount;
	@Mapped(wa = "ACTION_TYPE", xm = "")
	private String actionType;
	@Mapped(wa = "ACTION_TIME", xm = "common.serverTime")
	private String actionTime;
	@Mapped(wa = "USER_INTENRALID", xm = "common.uid" )
	private String uid;
	@Mapped(wa = "USER_ACCOUNT", xm = "common.uid",enc=true)
	private String account;
	
	public String getRelatedAccountType() {
		return relatedAccountType;
	}

	public void setRelatedAccountType(String relatedAccountType) {
		this.relatedAccountType = relatedAccountType;
	}

	public String getRelatedAccountUid() {
		return relatedAccountUid;
	}

	public void setRelatedAccountUid(String relatedAccountUid) {
		this.relatedAccountUid = relatedAccountUid;
	}

	public String getRelatedAccount() {
		return relatedAccount;
	}

	public void setRelatedAccount(String relatedAccount) {
		this.relatedAccount = relatedAccount;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getActionTime() {
		return actionTime;
	}

	public void setActionTime(String actionTime) {
		this.actionTime = actionTime;
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

	public static Item getOpCode() {
		return DataAssembler.getOpCode(Dict.OP_RELATED_ACCOUNT);
	}
	
	public static Item getQueryOpCode() {
		return DataAssembler.getOpCode(Dict.OP_QUERY_RELATED_ACCOUNT);
	}

	@Override
	public void fixInfo() {
        setActionTime( StringUtils.isBlank(getActionTime()) ? null :getActionTime().substring(0, 10));
		setAccount(StringUtils.isBlank(getUid()) ? null : GuavaCache.getAccountByUid(getUid()));

	}
	
	
}
