package com.ximalaya.wa.sender.task;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ximalaya.wa.helper.ValuesHolder;
import com.ximalaya.xdcs.client.util.ErrorlogSpanUtil;

import avro.shaded.com.google.common.collect.Lists;

/**
 * 定是监控备份文件信息，确认网安数据上报
 * @author stan.she
 *
 */
@Component
public class FileMonitorTask {
	
	private final static List<String> opcodes = Lists.newArrayList("ACCOUNT","LOGIN","MEDIABROWSELOG"
								  ,"COMMENT","UPDOWNLOADLOG","COLLECTION","SUBSCRIBEINFO"
								  ,"MEDIASHAREFORWARDING","REWARD","MASKMESSAGE","MEDIASEARCHINFO");
	
	private final String appName = "wa-file-monitor"; 
	private final String serviceId = "wa"; 
	private final String eventType = "monitor"; 
	private final String logIdentity = "file"; 
	
	private String bakDir = ValuesHolder.getValue("${back.up.dir}");
	
	@Scheduled(cron = "0 30 8 1/1 * ?")
	public void taskJob() {
		
		// bakDir + 时间,判断两天的数据，昨天和今天凌晨的
		
		File yesDirFile = new File(bakDir + getDateDir(getPrexDay(new Date())));
		if (!yesDirFile.exists()) {
			// 报警
			ErrorlogSpanUtil.append(appName, serviceId, eventType, logIdentity, "亲，网安上报没有报送文件！还皮不？");
		}
		
		Map<String,Integer> map = new HashMap<>();
		
		File[] files = yesDirFile.listFiles();
		
		for (File file : files) {
			
			if (file.isFile()) {
				String opcode = file.getName().split("_")[1];
				if (opcodes.contains(opcode)) {
					
					if (map.get(opcode) == null) {
						map.put(opcode, 1);
					}else{
						Integer num = map.get(opcode);
						map.put(opcode, ++num);
					}
					
				}
			}
		}
		
		for (String opcode : opcodes) {
			if (map.get(opcode) == null) { // || map.get(opcode) < 某个值！
				// 报警
				ErrorlogSpanUtil.append(appName, serviceId, eventType, logIdentity, "亲，网安上报["+ opcode +"]没有报送文件！还皮不？");
			}
		}
		
		
	}
	
	private static String getDateDir(Date date) {
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		String dateString = format.format(date);
		return dateString + "/";
	}
	
	public static Date getPrexDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return calendar.getTime();
	}
	
	public static void main(String[] args) {
		String cmd = "curl http://sh-nh-b2-2-o14-app-9-195:3387/wa/function/zip_and_upload?opcode={0};";
		
		for (String opcode : opcodes) {
			System.out.println(cmd.replace("{0}", opcode));
		}
		
		
		
	}
	
	
}
