package com.ximalaya.wa.collector.core;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import com.ximalaya.wa.converter.WaConverter;
import com.ximalaya.wa.model.xml.MonitorResult;
import com.ximalaya.wa.model.xml.ResultInfo;

public class MonitorCollector extends Collector {

	@Override
	public void afterPropertiesSet() throws Exception {

		if (type == null)
			throw new RuntimeException("type must config!");
		this.queueName = StringUtils.uncapitalize(type) + "MonitorQueue";
        LOG = LoggerFactory.getLogger(MonitorCollector.class.getName() + ":" + type);

		Field field = ReflectionUtils.findField(DataHub.class, queueName);
		queue = (BlockingQueue) field.get(DataHub.class);
		enqueueTaskInFile();
	}

	@Override
	protected void toXmlFile(List datas) {

		if (CollectionUtils.isEmpty(datas))
			return;
		try {
			List<ResultInfo<MonitorResult>> results = WaConverter.convertToMonitorResult(datas);
			for (ResultInfo<MonitorResult> result : results) {
				try {
					write(result);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);

		}
	}
	
	public static void main(String[] args) {
		System.out.println(StringUtils.uncapitalize("PaymentOrder"));
	}

}
