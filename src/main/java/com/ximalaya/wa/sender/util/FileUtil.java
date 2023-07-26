package com.ximalaya.wa.sender.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.nali.common.util.StringUtil;
import com.ximalaya.data.event.util.NumberedKeyMapTransfer;
import com.ximalaya.wa.helper.ValuesHolder;
import com.ximalaya.wa.sender.model.Constant;

/**
 * 
 * @author nali 备份；删除目录； 文件名；文件夹文件名
 */
public class FileUtil {

	private static final Logger	logger			= LoggerFactory.getLogger(FileUtil.class);

	private static final String	BACKUP_SUFFIX	= ".bak";

//	private static String		XML_STORE_DIR	= ValuesHolder.getValue("${xml.store.dir}");

	private static String		BACKUP_DIR		= ValuesHolder.getValue("${back.up.dir}");

	private static String		ZIP_DIR			= ValuesHolder.getValue("${zip.dir}");

	private static String		QUERY_DIR		= ValuesHolder.getValue("${query.dir}");

	private static String		MONITOR_DIR		= ValuesHolder.getValue("${monitor.dir}");

	private static String		MANAGEMENT_DIR	= ValuesHolder.getValue("${management.dir}");
	
	

	public static void fileCopy(String srcFilePath, String destFilePath) throws IOException {

		InputStream src = new BufferedInputStream(new FileInputStream(new File(srcFilePath)));
		OutputStream dest = new BufferedOutputStream(new FileOutputStream(new File(destFilePath)));

		byte[] b = new byte[1024];
		int len = 0;
		while ((len = src.read(b)) != -1) {
			dest.write(b, 0, len);
		}

		dest.flush();
		src.close();
		dest.close();

	}

	public static boolean backUpFile(String filePath) {

		boolean flag = false;

		File file = new File(filePath);
		if (!file.exists()) {
			logger.error("the file {} not exist.", filePath);
			return flag;
		}

		try {
			String dir = BACKUP_DIR + getDateDir(new Date());
			
			// 备份路径不存在，创造备份路径
			File backUpFileDirec = new File(dir);
			if (!backUpFileDirec.exists()) {
				backUpFileDirec.mkdirs();
			}

			String backUpFilePath = dir + parse(filePath + BACKUP_SUFFIX);
			File backUpFile = new File(backUpFilePath);

			if (!backUpFile.exists()) {
				backUpFile.createNewFile();
				fileCopy(filePath, backUpFilePath);
				flag = true;
				logger.info("back up file success,filename : {}",(filePath + ".bak"));
			}

		} catch (Exception e) {
			flag = false;
			logger.error("back up file failure,filePath : {},e : {}", filePath, e.getMessage());
		}

		return flag;
	}

	public static void backUpFileInBatch(List<String> filePaths) {
		long begin = System.currentTimeMillis();

		logger.info("back up task begin...");

		for (String filePath : filePaths) {
			backUpFile(filePath);
		}

		long end = System.currentTimeMillis();
		
		logger.info("back up task end,total num:{},costs time:{}s", filePaths.size(), (end - begin) / 1000);
	}

	public static List<String> getTotalXmlFile(String filePath) {
		List<String> list = new ArrayList<String>();

		File file = new File(filePath);
		String[] fileNames = file.list();

		if (fileNames.length < 1) {
			return null;
		}

		for (int i = 0; i < fileNames.length; i++) {
			String fileName = fileNames[i];
			if (FileUtil.isXmlFile(fileName)) {
				list.add(filePath + fileName);
			}
		}

		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		return list;
	}

	public static List<String> getTotalZipXmlFile() {
		List<String> list = new ArrayList<String>();

		File file = new File(ZIP_DIR);
		String[] fileNames = file.list();

		if (fileNames.length < 1) {
			return null;
		}

		for (int i = 0; i < fileNames.length; i++) {
			String fileName = fileNames[i];
			if (FileUtil.isZipFile(fileName)) {
				list.add(ZIP_DIR + fileName);
			}
		}

		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		return list;
	}

	public static void deleteDir(File dir) {
		if (dir.isFile()) {
			dir.delete();
		} else {
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteDir(files[i]);
			}
			dir.delete();
		}
	}

	public static String getQueryXMLDir(String opCode, String msgType, String msgId) {

		File queryFile = new File(QUERY_DIR);
		if (!queryFile.exists()) {
			queryFile.mkdirs();
		}

		String fileName = Joiner.on('_').join(new String[] { Constant.APPLICATION_CODING, opCode, msgType, msgId,
				String.format("%04d", (int) (1 + Math.random() * 9998)), "V2.xml" });

		return QUERY_DIR + fileName;
	}

	public static String getMonitorXMLDir(String opCode, String msgType,String msgId) {

		File monitorFile = new File(MONITOR_DIR);
		if (!monitorFile.exists()) {
			monitorFile.mkdirs();
		}

		String fileName = Joiner.on('_').join(new String[] { Constant.APPLICATION_CODING, opCode, msgType, msgId,
				String.format("%04d", (int) (1 + Math.random() * 9998)), "V2.xml" });

		return MONITOR_DIR + fileName;
	}

	public static String getManageXMLDir(String opCode, String msgType,String msgId) {

		File manageFile = new File(MANAGEMENT_DIR);
		if (!manageFile.exists()) {
			manageFile.mkdirs();
		}

		String fileName = Joiner.on('_').join(new String[] { Constant.APPLICATION_CODING, opCode, msgType, msgId,
				String.format("%04d", (int) (1 + Math.random() * 9998)), "V2.xml" });

		return MANAGEMENT_DIR + fileName;
	}

	public static String parse(String srcFile) {

		int location = srcFile.lastIndexOf("/");
		if (location != -1) {
			return srcFile.substring(location + 1);
		}

		return srcFile;
	}

	private static String getDateDir(Date date) {
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		String dateString = format.format(date);
		return dateString + File.separator;
	}
	
	/**
	 * 创建file，自动创建目录
	 * 
	 * @param directoryPath
	 *            文件目录
	 * @param fileName
	 *            文件名
	 * @return File
	 */
	public static File newFile(String directoryPath, String fileName) {
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		File file = new File(directory.getAbsoluteFile() + File.separator
				+ fileName);
		return file;
	}
	
	public static boolean isXmlFile(String filePath) {
		Pattern pattern = Pattern.compile(".*\\.xml$");
		Matcher matcher = pattern.matcher(filePath);
		return matcher.matches();
	}

	public static boolean isZipFile(String filePath) {
		Pattern pattern = Pattern.compile(".*\\.zip$");
		Matcher matcher = pattern.matcher(filePath);
		return matcher.matches();
	}

	public static void main(String[] args) {

		String message = "{\"25\":{\"uid\":\"79097373\",\"x-span-source\":\"x-java\",\"x-server-costtime\":\"121\",\"endTime\":\"157870\",\"token\":\"189d4a6d056ed04cd108b1a0811cefb700dc\"},\"72\":{\"132\":\"51743\",\"78\":1497339164949,\"92\":\"6241284510917318506\",\"83\":null,\"73\":\"xm_source:search&xm_term:小心念雷文\",\"85\":null},\"74\":{\"11\":\"未知\",\"13\":\"00000000-293a-5560-ffff-ffffce7e7eee\",\"79\":1497339166456,\"15\":\"OPPOR9m\",\"16\":\"HHf2BC3A\",\"17\":\"OPPO\",\"18\":\"WIFI\",\"19\":\"Android22\",\"130\":\"861079031322896\",\"4\":\"and-f6\",\"90\":79097373,\"8\":\"android\",\"9\":\"6.3.6\",\"81\":103.42359924316406,\"93\":\"200\",\"84\":23.350303649902344,\"75\":\"112.116.127.72\",\"20\":1920,\"86\":null,\"21\":1080},\"76\":{\"133\":\"哭湿枕巾[偷笑][哈哈]\",\"39\":\"11060723\",\"139\":\"157870\",\"131\":null}}";
		NumberedKeyMapTransfer transfer = NumberedKeyMapTransfer.apply("comment-track");// Thread

		// safe
		Map result = transfer.trans(message);
		System.out.println(result);
		
	}

}
