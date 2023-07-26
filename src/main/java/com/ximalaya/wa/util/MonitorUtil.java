package com.ximalaya.wa.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.ximalaya.wa.annotation.Mapped;
import com.ximalaya.wa.collector.core.WaContext;
import com.ximalaya.wa.model.xml.Item;
import com.ximalaya.wa.monitor.ConditionLoader;

public class MonitorUtil {
	private static final String METHOD_GET_OPCODE = "getOpCode";

	/**
	 * 判断是否布控，并返回opid
	 * @param bean
	 * @return
	 * @throws Exception
	 */
	public static String getPmId(Object bean) throws Exception{
		
		if(bean==null) return null;
		
		String pmId = null;
		Map<String,HashMap<String,String>> conditionsMap = ConditionLoader.getConditionsMap();
		
		Class clazz = bean.getClass();	
		Method getOpCode = clazz.getMethod(METHOD_GET_OPCODE);
		Item item = (Item) getOpCode.invoke(clazz);
		
		String opcode = item.getVal();
				
		Map<Field, Mapped> fieldInfo = WaContext.getAnnotatedFields(clazz);
		for (Map.Entry<Field, Mapped> annField : fieldInfo.entrySet()) {
			String key = opcode+"_"+annField.getValue().wa();
			HashMap<String,String> map = conditionsMap.get(key);
			if(map==null)continue;
			annField.getKey().setAccessible(true);
			String value = (String) annField.getKey().get(bean);
			if(map.containsKey(value)){
				
				pmId = map.get(value);
				break;
			}
				

		}
	return pmId;
	}
}
