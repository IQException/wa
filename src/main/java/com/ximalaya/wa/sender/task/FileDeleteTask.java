package com.ximalaya.wa.sender.task;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ximalaya.wa.sender.util.FileUtil;

/**
 * 保留14天的最新的数据
 * 
 * @author nali
 *
 */
@Component
public class FileDeleteTask {

	private final Logger		logger	= LoggerFactory.getLogger(FileDeleteTask.class);

	private static String		BACKUP_DIR;

	private static Properties	properties;

	static {
		URL url = FileDeleteTask.class.getClassLoader().getResource("dirs.properties");
		try {
			properties = new Properties();
			properties.load(url.openStream());
			BACKUP_DIR = properties.getProperty("back.up.dir");
		} catch (IOException e) {
			throw new IllegalArgumentException("can not find config file dirs.properties");
		}
	}

	@Scheduled(cron = "0 20 1 1/1 * ?")
	public void taskJob() {

		logger.info("delete file task begin...");

		File file = new File(BACKUP_DIR);
		
		List<File> files = new ArrayList<>();
		
		for (File dateFile : file.listFiles()) {
			if (match(dateFile)) {
				files.add(dateFile);
			}
		}
		
		Collections.sort(files);
		
		while(files.size() > 14){
			File deletedFile = files.remove(0);
			FileUtil.deleteDir(deletedFile);
			logger.info("the file \'{}\' has deleted...",deletedFile.getName());
		}
		
		logger.info("delete file task end...");

	}

	private static boolean match(File file) {
		Pattern pattern = Pattern.compile("\\d{8}");
		Matcher matcher = pattern.matcher(file.getName());
		return matcher.matches();
	}
	
}
