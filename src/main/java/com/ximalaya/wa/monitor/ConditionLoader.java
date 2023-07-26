package com.ximalaya.wa.monitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.ximalaya.wa.dao.MonitorDao;
import com.ximalaya.wa.monitor.model.Condition;
@Component
public class ConditionLoader {
	private static Map<String,HashMap<String,String>> conditionsMap = Maps.newHashMap() ;

	@Autowired
	MonitorDao dao;
	@Scheduled(cron = "0 0/1 * * * ?")
	public void load(){
		Map<String,HashMap<String,String>> conditionsMapTmp = Maps.newHashMap();
		List<Condition> conditions = dao.loadAll();
		for(Condition condition :conditions){
			String key = condition.getOpcode()+"_"+condition.getConditionKey();
			HashMap<String,String> map = conditionsMapTmp.get(key);
			if(map!=null){
				map.put(condition.getConditionValue(), condition.getPmId());
			}else{
				map = new HashMap<String,String>();
				map.put(condition.getConditionValue(), condition.getPmId());
				conditionsMapTmp.put(key, map);
			}
		}
		
		conditionsMap = conditionsMapTmp;
		
	}
	public static Map<String,HashMap<String,String>>  getConditionsMap() {
		return conditionsMap;
	}
	
}
