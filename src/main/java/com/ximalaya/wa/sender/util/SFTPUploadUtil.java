package com.ximalaya.wa.sender.util;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.ximalaya.wa.helper.ValuesHolder;

/**
 * 
 * @author nali REPORT服务需要一次上传所有的文件，JINKOU上传的是生成的文件，要求在两分钟之内必须得上传。逻辑上有区别。
 *
 */
public class SFTPUploadUtil {

	private static final Logger								logger		= LoggerFactory.getLogger(SFTPUploadUtil.class);

	public static final String								REPORT		= "report";
	public static final String								JINGKOU		= "jingkou";

	// sftp server config
	private static final Map<String, Map<String, Object>>	paramMap	= new HashMap<>();

	static {

		// reprot
		Map<String, Object> submitMap = new HashMap<>();
		submitMap.put("ip", "220.248.98.66");
		submitMap.put("port", 6666);
		submitMap.put("userName", "ximalaya");
		submitMap.put("password", "Ximalaya12#$");
		submitMap.put("remotePath", "/write/");
		paramMap.put(REPORT, submitMap);

		// jingkou
		Map<String, Object> jingKouMap = new HashMap<>();
		jingKouMap.put("ip", "220.248.98.66");
		jingKouMap.put("port", 6666);
		jingKouMap.put("userName", "jingkou");
		jingKouMap.put("password", "Jingkou12#$");
		jingKouMap.put("remotePath", "/write/");
		paramMap.put(JINGKOU, jingKouMap);

	}

	private static String BAK_DIR = ValuesHolder.getValue("${back.up.dir}");

	public static boolean uploadFileInBatch(String serverName, String dir) throws JSchException {

		boolean flag = false;
		
		File dirFile = new File(dir);
		File[] files = dirFile.listFiles();
		if (files.length < 1) {
			logger.info("has not file yet...");
			return flag;
		}
		
		List<String> list = new ArrayList<>();
		for (File file : files) {
			list.add(file.getAbsolutePath());
		}
		
		flag = uploadFile(serverName, list);

		return flag;
	}
	
	public static boolean uploadFile(String serverName,String filePath) throws JSchException{
		List<String> list = new ArrayList<>(1);
		list.add(filePath);
		return uploadFile(serverName, list);
	}

	/**
	 * 
	 * @param serverName
	 * @param filePathes 文件全名 dir+fileName
	 * @return
	 * @throws JSchException
	 */
	public static boolean uploadFile(String serverName, List<String> filePathes) throws JSchException {
		
		if (CollectionUtils.isEmpty(filePathes)) {
			logger.error("there has no file to upload...");
			return false;
		}

		// server config
		Map<String, Object> map = paramMap.get(serverName);
		String ip = (String) map.get("ip");
		int port = (int) map.get("port");
		String userName = (String) map.get("userName");
		String password = (String) map.get("password");
		String remotePath = (String) map.get("remotePath");

		boolean flag = false;
		
		// get client
		long begin = System.currentTimeMillis();

		logger.info("upload task begin...");

		JSch jsch = new JSch();
		Session session = jsch.getSession(userName, ip, port);

		if (session == null) {
			logger.error("connect the sftp server error...");
		} // 如果服务器连接不上，则抛出异常

		ChannelSftp sftp = null;
		try {
			session.setPassword(password); // 设置密码
			session.setConfig("StrictHostKeyChecking", "no"); // 设置第一次登陆的时候提示，可选值：(ask | yes | no)
			session.connect(30000); // 设置登陆超时时间
			Channel channel = session.openChannel("sftp");
			channel.connect(10000); 
			sftp = (ChannelSftp) channel;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return flag;
		} // 连接超时，处理异常，否则定时任务抛出异常会一直执行
		

//		 try {
//			 sftp.cd(remotePath);
//			 Vector v = sftp.ls("*.zip");
//		 	 for (int i = 0; i < v.size(); i++) {
//		 		System.out.println(v.get(i));
//			 }
//		 } catch (SftpException e) {
//			 logger.error("the remote sftp path {} not exist", remotePath);
//		 }// 进入服务器指定的文件夹


		// upload
		int num = 0;
		for (String fileName : filePathes) {
			if (isZipFile(fileName) || isXmlFile(fileName)) {
				try {
					long time1 = System.currentTimeMillis();
					sftp.put(fileName, remotePath);
					long time2 = System.currentTimeMillis();
					logger.info("upload file success,fileName:{},cost time:{}s",FileUtil.parse(fileName), (time2 - time1) / 1000);
					// upload file
					
					File bakFile = new File(BAK_DIR + getDateDir(new Date()));
					if (!bakFile.exists()) {
						bakFile.mkdirs();
					}
					String fileBakDir = BAK_DIR + getDateDir(new Date()) + FileUtil.parse(fileName);
					File mFile = new File(fileBakDir);
					if (!mFile.exists()) {
						File file = new File(fileName);
						if (file.renameTo(mFile)) {
							logger.info("move file success,filename:{}", FileUtil.parse(fileName));
						}
					} // 文件移动到的目录需要按日期分类，并文件已存在，不移动
					num++;
				} catch (SftpException e) {
					logger.error("upload file error,fileName : {}", fileName,e);
				}
			}
		}

		logger.info("upload total num : {}", num);

		long end = System.currentTimeMillis();

		logger.info("upload task end,cost time : {}s", (end - begin) / 1000);

		return flag;

	}

	private static String getDateDir(Date date) {
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		String dateString = format.format(date);
		return dateString + "/";
	}

	public static boolean isZipFile(String filePath) {
		Pattern pattern = Pattern.compile(".*\\.zip$");
		Matcher matcher = pattern.matcher(filePath);
		return matcher.matches();
	}

	public static boolean isXmlFile(String filePath) {
		Pattern pattern = Pattern.compile(".*\\.xml$");
		Matcher matcher = pattern.matcher(filePath);
		return matcher.matches();
	}
	
	

	public static void main(String[] args) throws Exception {
		String dir = "/Users/nali/Desktop/tmp/manage/1390008_FREEZEUSERS_2_010000150284938389993_5900_V2.xml";
//		uploadFileInBatch("report", dir+filePath);
		uploadFile("report",dir);
	}

}
