package com.ximalaya.wa.service;

import java.util.Map;

public interface IWAManageService {

	public void manage(String opCode, Map<String, String> criteria, String msgId, String opId);
	
}
