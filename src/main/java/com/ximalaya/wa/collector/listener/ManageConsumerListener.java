package com.ximalaya.wa.collector.listener;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.ximalaya.wa.config.Dict;
import com.ximalaya.wa.converter.WaConverter;
import com.ximalaya.wa.model.xml.CommonData;
import com.ximalaya.wa.model.xml.Data;
import com.ximalaya.wa.model.xml.ManageResult;
import com.ximalaya.wa.sender.model.Constant;
import com.ximalaya.wa.sender.util.FileUtil;
import com.ximalaya.wa.sender.util.SFTPUploadUtil;
import com.ximalaya.wa.util.DataUtil;
import com.ximalaya.wa.util.OxmUtil;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

@Component
public class ManageConsumerListener {
	
private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${kafka.zk}")
	private String kafkaZkURL;
	
	@Value("${kafka.topic}")
	private String kafkaTopic;
	
	@PostConstruct
	public void work() {
		
		ExecutorService executor = Executors.newCachedThreadPool();
		
		Properties consumerConfig = new Properties();
		consumerConfig.put("zookeeper.connect", kafkaZkURL);
		consumerConfig.put("zookeeper.session.timeout.ms", "10000");
		consumerConfig.put("zookeeper.sync.time.ms", "1000");
		consumerConfig.put("group.id", "wa-manage");
		consumerConfig.put("auto.offset.reset", "smallest");// kafka从第一行开始收消息
		
		ConsumerConnector consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(consumerConfig));
		Map<String, Integer> topicCount = Maps.newHashMap();
		String[] topics = kafkaTopic.split(",");
		int numberOfStreams = 1;
		for (String topic : topics) {
			topicCount.put(topic, numberOfStreams);
		}
		Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams = consumer.createMessageStreams(topicCount);
		if (messageStreams != null) {
			for (Map.Entry<String, List<KafkaStream<byte[], byte[]>>> entry : messageStreams.entrySet()) {
				final String topic = entry.getKey();
				logger.info("subscribe topic:"+topic);
				List<KafkaStream<byte[], byte[]>> value = entry.getValue();
				if (value != null) {
					for (final KafkaStream<byte[], byte[]> stream : value) {
						
						Runnable task = () -> {
							while (true) {
								try {
									ConsumerIterator<byte[], byte[]> iter = stream.iterator();
									while (iter.hasNext()) {
										try {
											String msg = new String(iter.next().message(), Charsets.UTF_8);
											logger.info("receive message : {}",msg);
											JSONObject obj = JSON.parseObject(msg);
											if (obj == null) {
												continue;
											}
											write2XmlAndUpload(obj); // write to xml and upload
										} catch (Exception e) {
											logger.error(e.getMessage(), e);
										}
									}
								} catch (Exception e) {
									logger.error(e.getMessage(), e);
								}
							}
						};
						executor.execute(task);
					}
				}
			}
		}
	}
	
	private void write2XmlAndUpload(JSONObject obj){
		
			// parse
			String opCode = obj.getString("opCode");
			
			String msgId = obj.getString("msgId");
			String opId = obj.getString("opId");
			String errorCode = obj.getString("ERRORCODE");
			
			String failReason = "管控成功";
			String remark = "管控成功";
			
			if (!errorCode.equals("0")) {
				failReason = obj.getString("FAIL_REASON");
				remark = obj.getString("REMARK");
			}
			
			// write to xml and upload
			try {
				CommonData commonResult = WaConverter.getCommonData(opCode, Dict.MSG_RESULT, msgId, opId);
				List<Data> resultData = DataUtil.getManageResultData(errorCode, failReason, remark);
				String dir = FileUtil.getMonitorXMLDir(opCode, Constant.WA_RESULT, msgId);
				OxmUtil.toXML(new ManageResult(commonResult.getCommonDatas(), resultData), new File(dir)); // write
				
				SFTPUploadUtil.uploadFile(SFTPUploadUtil.JINGKOU, dir); // upload
				
			} catch (Exception e) {
				logger.error("send xml error,opCode : {},data:{}",opCode,obj.toString(),e);
			}
	}
	

}
