package com.ximalaya.wa.service;

import java.util.Map;

public interface IWAQueryService {
	
	public void query(String opCode, String msgId, Map<String,String> criteria);

}
