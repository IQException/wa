package com.ximalaya.wa.service;

import java.util.Map;

public interface IWAMonitorService {
	
	public void monitor(String opCode,Map<String,String> criteria,String msgId, String opId);

}
