package com.ximalaya.wa.model;

import org.apache.commons.lang.StringUtils;

import com.ximalaya.wa.annotation.Mapped;
import com.ximalaya.wa.assembler.DataAssembler;
import com.ximalaya.wa.cache.RedisCache;
import com.ximalaya.wa.config.Dict;
import com.ximalaya.wa.model.xml.Item;

public class Share  extends BaseModel {

	@Mapped(wa = "FILE_ID", xm = "data.trackId")
	private String trackId;
	@Mapped(wa = "FILE_MD5", xm = "")
	private String fileMd5;
	@Mapped(wa = "FILE_URL", xm = "",enc=true)
	private String url;
	@Mapped(wa = "REMARK", xm = "data.shareContent")
	private String content;
	@Mapped(wa = "REMARK_URL", xm = "",enc=true)
	private String remarkUrl;
	@Mapped(wa = "REMARK_USERID", xm = "")
	private String atUid ;
	@Mapped(wa = "REMARK_NAME", xm = "data.shareTpName",enc=true)
	private String tpName;
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	public String getFileMd5() {
		return fileMd5;
	}
	public void setFileMd5(String fileMd5) {
		this.fileMd5 = fileMd5;
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
	public String getRemarkUrl() {
		return remarkUrl;
	}
	public void setRemarkUrl(String remarkUrl) {
		this.remarkUrl = remarkUrl;
	}
	public String getAtUid() {
		return atUid;
	}
	public void setAtUid(String atUid) {
		this.atUid = atUid;
	}
	public String getTpName() {
		return tpName;
	}
	public void setTpName(String tpName) {
		this.tpName = tpName;
	}
	public static Item getOpCode() {
		return DataAssembler.getOpCode(Dict.OP_SHARE);
	}

	@Override
	public void fixInfo() {
		super.fixInfo();
        setUrl(DataAssembler.URL_PREFIX+getTrackId());
        setFileMd5(StringUtils.isBlank(getTrackId()) ? null :RedisCache.getMd5ByTrackId(getTrackId()));

	}
}
