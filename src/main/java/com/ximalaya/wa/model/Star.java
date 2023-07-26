package com.ximalaya.wa.model;

import org.apache.commons.lang.StringUtils;

import com.ximalaya.wa.annotation.Mapped;
import com.ximalaya.wa.assembler.DataAssembler;
import com.ximalaya.wa.cache.RedisCache;
import com.ximalaya.wa.config.Dict;
import com.ximalaya.wa.model.xml.Item;

public class Star  extends BaseModel {


	@Mapped(wa = "FILE_ID", xm = "data.trackId")
	private String trackId;
	@Mapped(wa = "FILE_MD5", xm = "")
	private String fileMd5;
	@Mapped(wa = "FILE_URL", xm = "",enc=true)
	private String url;
	@Mapped(wa = "ACTION_TYPE", xm = "")
	private String actionType;
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
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	public static Item getOpCode() {
		return DataAssembler.getOpCode(Dict.OP_STAR);
	}
	@Override
	public void fixInfo() {
		super.fixInfo();
		//只有点赞
		setActionType(Dict.ACTION_STAR_LIKE);
        setUrl(DataAssembler.URL_PREFIX+getTrackId());
        setFileMd5(StringUtils.isBlank(getTrackId()) ? null :RedisCache.getMd5ByTrackId(getTrackId()));
	}
}
