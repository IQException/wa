package com.ximalaya.wa.model;

import org.apache.commons.lang.StringUtils;

import com.ximalaya.wa.annotation.Mapped;
import com.ximalaya.wa.assembler.DataAssembler;
import com.ximalaya.wa.cache.RedisCache;
import com.ximalaya.wa.config.Dict;
import com.ximalaya.wa.model.xml.Item;

public class UpDownload  extends BaseModel{
	@Mapped(wa = "FILE_ID", xm = "data.trackId")
	private String trackId;
	@Mapped(wa = "FILE_NAME", xm = "data.trackTitle")
	private String trackTitle;
	@Mapped(wa = "FILE_TYPE", xm = "")
	private String fileType;
	@Mapped(wa = "FILE_DESCRIBE", xm = "data.trackIntro",enc=true)
	private String trackInfo;
	@Mapped(wa = "FILE_FORM", xm = "data.audioFileFormat")
	private String fileFormat;
	@Mapped(wa = "FILE_SIZE", xm = "data.audioFileSize")
	private String fileSize;
	@Mapped(wa = "FILE_MD5", xm = "data.audioFileMd5")
	private String  fileMd5;
	@Mapped(wa = "FILE_CONTENT", xm = "data.trackIntro",enc=true)
	private String fileContent;
	@Mapped(wa = "FILE_URL", xm = "data.trackPlayPath",enc=true)
	private String url;
	@Mapped(wa = "ACTION_TYPE", xm = "")
	private String actionType;
	
	
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	public String getTrackTitle() {
		return trackTitle;
	}
	public void setTrackTitle(String trackTitle) {
		this.trackTitle = trackTitle;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getTrackInfo() {
		return trackInfo;
	}
	public void setTrackInfo(String trackInfo) {
		this.trackInfo = trackInfo;
	}
	public String getFileFormat() {
		return fileFormat;
	}
	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getFileMd5() {
		return fileMd5;
	}
	public void setFileMd5(String fileMd5) {
		this.fileMd5 = fileMd5;
	}
	public String getFileContent() {
		return fileContent;
	}
	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public static Item getOpCode() {
		return DataAssembler.getOpCode(Dict.OP_UPDOWNLOAD);
	}
	@Override
	public void fixInfo() {
		super.fixInfo();
		//只有声音
		setFileType(Dict.FILETYPE_TRACK);
        setUrl(DataAssembler.URL_PREFIX+getTrackId());
        setFileMd5(StringUtils.isBlank(getTrackId()) ? null :RedisCache.getMd5ByTrackId(getTrackId()));
		if(getActionType().equals(Dict.ACTION_UD_DOWNLOAD)){
			setFileSize(StringUtils.isBlank(getTrackId()) ? null :RedisCache.getFileSizeByTrackId(getTrackId()));
			setFileFormat(StringUtils.isBlank(getTrackId()) ? null:RedisCache.getFileFormatByTrackId(getTrackId()));
		}
	}
}
