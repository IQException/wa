package com.ximalaya.wa.model;

import com.ximalaya.wa.annotation.Mapped;
import com.ximalaya.wa.assembler.DataAssembler;
import com.ximalaya.wa.config.Dict;
import com.ximalaya.wa.model.xml.Item;

public class Comment extends BaseModel{

	@Mapped(wa = "COMMENT_ID", xm = "data.id")
	private String commentId;
	@Mapped(wa = "MEDIA_ID", xm = "data.trackId",enc=true)
	private String trackId;
	@Mapped(wa = "CONTENT", xm = "data.content",enc=true)
	private String content;
	@Mapped(wa = "URL", xm = "" ,enc=true)
	private String url;
	public String getCommentId() {
		return commentId;
	}
	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public static Item getOpCode() {
		return DataAssembler.getOpCode(Dict.OP_COMMENT);
	}
	public static Item getQueryOpCode() {
		return DataAssembler.getOpCode(Dict.OP_QUERY_COMMENT);
	}
	@Override
	public void fixInfo() {

		super.fixInfo();
		setUrl(DataAssembler.URL_PREFIX+getTrackId());
	}
}
