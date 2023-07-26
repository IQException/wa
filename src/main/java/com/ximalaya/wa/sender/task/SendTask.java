package com.ximalaya.wa.sender.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.JSchException;
import com.ximalaya.wa.helper.ValuesHolder;
import com.ximalaya.wa.sender.util.FileUtil;
import com.ximalaya.wa.sender.util.SFTPUploadUtil;
import com.ximalaya.wa.sender.util.ZipCompressUtil;

/**
 * 10分钟上报一次
 * 
 * @author nali
 *
 */
@Component
public class SendTask {

	private final Logger	logger	= LoggerFactory.getLogger(SendTask.class);

	private boolean			running	= false;

	@Value("${xml.store.dir}")
	private String			XML_STORE_DIR;

	@Value("${zip.dir}")
	private String			ZIP_DIR;

	@Scheduled(cron = "0 0/10 * * * ?")
	public void taskJob() {

		if (running) {
			logger.info("the timed task is still executing...");
			return;
		}
		running = true; // 定时任务开始执行

		logger.info("send task begin...");

		long begin = System.currentTimeMillis();

		// 1.拿到所有的XML文件，压缩
		List<String> xmlFileList = FileUtil.getTotalXmlFile(XML_STORE_DIR);
		

		if (xmlFileList == null) {
			logger.info("the dir to store xml file has no file ...");
			running = false;
			return;
		}

		ZipCompressUtil.zipCompressInBatch(xmlFileList, ZIP_DIR);

		// 3.上传文件
		try {
			SFTPUploadUtil.uploadFileInBatch(SFTPUploadUtil.REPORT, ZIP_DIR);
		} catch (JSchException e) {
			logger.error(e.getMessage(),e);
		}

		long end = System.currentTimeMillis();

		logger.info("send task end,cost time : {}s", (end - begin) / 1000);

		running = false; // 定时任务结束

	}

}
