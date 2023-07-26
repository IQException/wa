package com.ximalaya.wa.sender.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ximalaya.wa.helper.ValuesHolder;

public class FTPUploadUtil {

	private static final Logger	logger		= LoggerFactory.getLogger(FTPUploadUtil.class);

	public static final String	HOST		= "220.248.98.66";
	public static final int		PORT		= 6666;
	public static final String	USERNAME	= "ximalaya";
	public static final String	PASSWORD	= "Ximalaya12#$";
	public static final String	REMOTEPATH	= "/write";

	private static String		ZIP_DIR		= ValuesHolder.getValue("${zip.dir}");
	
	
	

	public static boolean uploadFileInBatch() {

		long begin = System.currentTimeMillis();

		boolean flag = true;

		File file = new File(ZIP_DIR);
		File[] zipFIles = file.listFiles();
		if (zipFIles.length < 1) {
			logger.info("has not compressed xml file yet...");
			return !flag;
		}

		FTPClient client = new FTPClient();
		client.setControlEncoding("UTF-8");

		try {
			client.connect(HOST, PORT); // 连接FTP服务器；如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
			client.login(USERNAME, PASSWORD); // 登录

			// 查看ftp调试信息
			// client.addProtocolCommandListener(new PrintCommandListener(new
			// PrintWriter(System.out)));

			// 是否成功登录FTP服务器
			int reply = client.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				client.disconnect();
				logger.error("connect ftp server error...");
				return !flag;
			}

			client.setFileType(FTPClient.BINARY_FILE_TYPE);

			// 文件服务器的路径，如果没有创建，需要一级一级的创建目录
			if (!client.changeWorkingDirectory(REMOTEPATH)) {
				String[] pathes = REMOTEPATH.split("/");
				String dir = "";
				for (int i = 0; i < pathes.length; i++) {

					if (pathes[i] == null || pathes[i].trim().equals(""))
						continue;// 数组第一个元素为""，跳过
					dir += "/" + pathes[i];

					if (!client.changeWorkingDirectory(dir)) {
						if (!client.makeDirectory(dir)) {// 创建目录
							logger.info("mkdir {} error...", dir);
							return !flag;// 创建失败，返回false
						}
					}

				}

				client.changeWorkingDirectory(dir);// 更换到远程目录
			}

			for (int i = 0; i < zipFIles.length; i++) {
				File zipFile = zipFIles[i];
				if (!uploadFile(client, zipFile.getAbsolutePath())) {
					logger.error("upload file error,zip file:{}", zipFile.getAbsolutePath());
					flag = false;
				}
			}

			client.logout();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		long end = System.currentTimeMillis();

		logger.info("the upload task costs time : {}s", (end - begin) / 1000);

		return flag;
	}

	/**
	 * 
	 * @param filePath
	 *            zip文件地址
	 * @return
	 */
	public static boolean uploadFile(FTPClient client, String filePath) {
		boolean is_successful = false;

		try {

			String[] dirs = filePath.split("/");
			String fileName = dirs[dirs.length - 1];
			InputStream in = new FileInputStream(new File(filePath));

			if (!client.storeFile(fileName, in)) {
				logger.error("upload file error,fileName:{}", fileName);
				return is_successful;
			}

			in.close();
			is_successful = true;

		} catch (Exception e) {
			is_successful = false;
			logger.error(e.getMessage(), e);
		}

		return is_successful;
	}

	public static void main(String[] args) {
		System.out.println(uploadFileInBatch());
	}

}
