package com.ximalaya.wa.model;

import org.apache.commons.lang.StringUtils;

import com.ximalaya.wa.annotation.Mapped;
import com.ximalaya.wa.assembler.DataAssembler;
import com.ximalaya.wa.cache.RedisCache;
import com.ximalaya.wa.config.Dict;
import com.ximalaya.wa.model.xml.Item;

public class Play  extends BaseModel {
	@Mapped(wa = "MEDIA_ID", xm = "data.trackId")
	private String trackId;
	@Mapped(wa = "MEDIA_NAME", xm = "data.trackTitle",enc=true)
	private String trackFile;
	@Mapped(wa = "MEDIA_URL", xm = "",enc=true)
	private String url;
	@Mapped(wa = "MEDIA_MD5", xm = "")
	private String fileMd5;
	@Mapped(wa = "MEDIA_TAG", xm = "data.trackTags",enc=true)
	private String trackTag;
	@Mapped(wa = "TRANSMIT_CONTENT", xm = "data.track_content")
	private String trackContent;
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	public String getTrackFile() {
		return trackFile;
	}
	public void setTrackFile(String trackFile) {
		this.trackFile = trackFile;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFileMd5() {
		return fileMd5;
	}
	public void setFileMd5(String fileMd5) {
		this.fileMd5 = fileMd5;
	}
	public String getTrackTag() {
		return trackTag;
	}
	public void setTrackTag(String trackTag) {
		this.trackTag = trackTag;
	}
	public String getTrackContent() {
		return trackContent;
	}
	public void setTrackContent(String trackContent) {
		this.trackContent = trackContent;
	}
	public static Item getOpCode() {
		return DataAssembler.getOpCode(Dict.OP_PLAY);
	}
	public static Item getQueryOpCode() {
		return DataAssembler.getOpCode(Dict.OP_QUERY_PLAY);
	}
	public static Item getMonitorOpCode() {
		return DataAssembler.getOpCode(Dict.OP_MONITOR_PLAY_ADD);
	}	
	@Override
	public void fixInfo() {
		super.fixInfo();
        setUrl(DataAssembler.URL_PREFIX+getTrackId());
		setFileMd5(StringUtils.isBlank(getTrackId()) ? null :RedisCache.getMd5ByTrackId(getTrackId()));

		
	}
}
