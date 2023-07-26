package com.ximalaya.wa.model;

import org.apache.commons.lang.StringUtils;

import com.ximalaya.wa.annotation.Mapped;
import com.ximalaya.wa.assembler.DataAssembler;
import com.ximalaya.wa.cache.GuavaCache;
import com.ximalaya.wa.config.Dict;
import com.ximalaya.wa.model.xml.Item;

public class Subscribe implements CommonProcessor{
	@Mapped(wa = "USER_CONCERNEDID", xm = "data.albumId,data.toUid")
	private String subscribeId;
	@Mapped(wa = "GROUPNAME", xm = "data.albumTitle")
	private String subscribeName;
	@Mapped(wa = "ACTION_TYPE", xm = "data.isSubscribe,data.isFollow")
	private String actionType;
	@Mapped(wa = "ACTION_TIME", xm = "common.serverTime")
	private String actionTime;
	@Mapped(wa = "USER_INTENRALID", xm = "common.uid" )
	private String uid;
	@Mapped(wa = "USER_ACCOUNT", xm = "common.uid",enc=true)
	private String account;
	public String getSubscribeId() {
		return subscribeId;
	}
	public void setSubscribeId(String subscribeId) {
		this.subscribeId = subscribeId;
	}
	public String getSubscribeName() {
		return subscribeName;
	}
	public void setSubscribeName(String subscribeName) {
		this.subscribeName = subscribeName;
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
		return DataAssembler.getOpCode(Dict.OP_SUBSCRIBE);
	}
	public void fixInfo() {
        setActionTime( StringUtils.isBlank(getActionTime()) ? null :getActionTime().substring(0, 10));
		setAccount(StringUtils.isBlank(getUid()) ? null : GuavaCache.getAccountByUid(getUid()));

	}

}
