package com.ximalaya.wa.model;

import com.ximalaya.wa.annotation.Mapped;
import com.ximalaya.wa.assembler.DataAssembler;
import com.ximalaya.wa.config.Dict;
import com.ximalaya.wa.model.xml.Item;

public class Bullet extends Comment {

	@Mapped(wa="VIDEO_LOCATION",xm="data.startTime")
	private String startTime ;

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	public static Item getMonitorOpCode() {
		return DataAssembler.getOpCode(Dict.OP_MONITOR_BULLET_ADD);
	}
}
