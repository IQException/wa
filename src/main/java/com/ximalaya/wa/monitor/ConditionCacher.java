package com.ximalaya.wa.monitor;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ConditionCacher {

//	private static final Map<String,HashMap<String,HashSet<String>>> conditionsMap = Maps.newConcurrentMap();
//	private static final Lock mapLock = new ReentrantLock();
//	private static final Lock conditionLock = new ReentrantLock();
//
//	static{
////		conditionsMap.put(Dict.op, value);
//	}
//	public static void add(String opcode,Map<String,String> conditionMap){
//		if(StringUtils.isNotBlank(opcode)||conditionMap==null)return ;
//		HashMap<String,HashSet<String>> conditions = conditionsMap.get(opcode);
//		if(conditions==null){
//			mapLock.lock();
//			try{
//				conditions = conditionsMap.get(opcode);
//				if(conditions==null){
//					conditions=Maps.newHashMap();
//					conditionsMap.put(opcode,conditions);
//				}
//			}finally{
//				mapLock.unlock();
//			}
//		}
//		for(Map.Entry<String, String> condition:conditionMap.entrySet()){
//			if(StringUtils.isNotBlank(condition.getValue())&&StringUtils.isNotBlank(condition.getKey())){
//				if(conditions.get(condition.getKey())==null){
//					conditionLock.lock();
//					try{
//						if(conditions.get(condition.getKey())==null){
//							conditions.put(condition.getKey(), (HashSet<String>) Collections.synchronizedSet(new HashSet<String>()));
//						}
//					}finally{
//						conditionLock.unlock();
//					}
//				}
////				.add(condition.getValue());
//			}
//		}
//	//	conditionMap.
//	}
//	
//	public static void remove(String opcode,Map<String,String> conditionMap){
//
//	}
//	public static Map<String, HashMap<String, HashSet<String>>> getConditionsMap() {
//		return conditionsMap;
//	}
	
}
