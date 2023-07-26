package com.ximalaya.wa.sender.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSchException;
import com.ximalaya.dtres.util.FilePathUtils;
import com.ximalaya.wa.assembler.DataAssembler;
import com.ximalaya.wa.helper.ValuesHolder;
import com.ximalaya.wa.sender.model.Constant;

/**
 * 
 * @author Stan.She 单个zip文件大小不得超过100M；
 *
 */
public class ZipCompressUtil {

	private static final Logger	logger			= LoggerFactory.getLogger(ZipCompressUtil.class);

	public static final int		MAX_SIZE		= 5 * 30 * 1024 * 1024;								// 设定最小压缩比为5

	public static String		ZIP_DIR			= ValuesHolder.getValue("${zip.dir}");

	public static void zipCompressInBatch(List<String> srcFiles, String zipPath) {

		int fileSize = srcFiles.size();

		long begin = System.currentTimeMillis();

		logger.info("the zip task begin...");

		// 分组一：业务类型
		Map<String, List<String>> typeMap = new HashMap<String, List<String>>();

		for (String srcFile : srcFiles) {

			String fileName = new File(srcFile).getName();
			String[] str = fileName.split("_");
			String businessType = str[1];
			if (CollectionUtils.isEmpty(typeMap.get(businessType))) {
				List<String> list = new ArrayList<String>();
				list.add(srcFile);
				typeMap.put(businessType, list);
			} else {
				typeMap.get(businessType).add(srcFile);
			}

		}

		int no = 0;
		for (String businessType : typeMap.keySet()) {

			// 分组二：压缩文件大小不能超过30M，这里是使用最小压缩比来保证压缩文件不超过30M，最小压缩比设为10
			Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();
			Iterator<String> iterator = typeMap.get(businessType).iterator();

			long totalSize = 0;
			int num = 1;
			while (iterator.hasNext()) {
				String fileName = iterator.next();
				File file = new File(fileName);

				if (totalSize > 0 && totalSize < MAX_SIZE) {
					totalSize += file.length();

					if (totalSize < MAX_SIZE) {
						List<String> list = map.get(num);
						list.add(fileName);
					} else {
						totalSize = 0;
						num++;
					}

				}

				if (totalSize == 0) {
					List<String> fileNames = new ArrayList<String>();
					fileNames.add(fileName);
					map.put(num, fileNames);
					totalSize += file.length();
				}

			}

			// 压缩文件
			for (int i = 1; i <= map.size(); i++) {
				no += map.get(i).size();
				String zipname = getFileName(businessType, Constant.WA_RESULT, i);
				zipCompress(zipPath, map.get(i), zipname);
				
				// 压缩后直接上传
				try {
					SFTPUploadUtil.uploadFile(SFTPUploadUtil.REPORT, ZIP_DIR + zipname);
				} catch (JSchException e) {
					logger.error("upload file : {} failured ...",zipname,e);
				}
			}
			
			logger.info("create zip file size : {}",map.size());

		}

		logger.info("check compress task,fileSize:{},no:{}", fileSize, no);

		long end = System.currentTimeMillis();

		logger.info("the zip task costs time :{}s,total num : {}", (end - begin) / 1000, srcFiles.size());

	}

	// 压缩一个文件
	public static void zipCompress(String srcFile, String zipname) {

		List<String> srcFiles = new ArrayList<String>();
		srcFiles.add(srcFile);
		zipCompress(ZIP_DIR,srcFiles, zipname);

	}

	public static boolean zipCompress(String zipPath,List<String> srcFiles, String zipname) {

		boolean is_successful = false;

		File file = new File(zipPath);
		if (!file.exists()) {
			file.mkdirs();
		}

		File zipFile = new File(zipPath + zipname);
		if (zipFile.exists()) {
			file.delete();
		}

		String[] fileNames = new String[srcFiles.size()];
		for (int i = 0; i < fileNames.length; i++) {
			fileNames[i] = parse(srcFiles.get(i));
		}

		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(zipPath + zipname));
			ZipOutputStream zos = new ZipOutputStream(bos);

			for (int i = 0; i < fileNames.length; i++) {

				String entryName = fileNames[i];
				ZipEntry entry = new ZipEntry(entryName);
				zos.putNextEntry(entry);

				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFiles.get(i)));

				byte[] b = new byte[1024];
				int len = 0;
				while ((len = bis.read(b)) != -1) {
					zos.write(b, 0, len);
				}
				bis.close();
				zos.closeEntry();

			}

			zos.flush();
			zos.close();
			is_successful = true;
			logger.info("create zip file : {},zip has file size : {}", zipname,srcFiles.size());
		} catch (Exception e) {
			logger.error("compress xml file failured,filenames : {}", srcFiles, e);
		} finally {
			// 压缩完成后删除源文件
			if (is_successful) {
				for (String srcFile : srcFiles) {
					File deleteFile = new File(srcFile);
					deleteFile.delete();
				}
			}
		}

		return is_successful;

	}
	
	public static boolean zipCompressDir(String dir,String zipname){
		
		boolean isSuccess = false;
		
		File file = new File(dir);
		if (!file.exists()) {
			logger.warn("dir {} has no file...",dir);
			return isSuccess;
		}
		
		List<String> fileList = new ArrayList<>();
		
		String[] files = file.list();
		for (String fileName : files) {
			fileList.add(FilePathUtils.assembleLocalFilePath(dir,fileName));
		}
		
		logger.info("start to create account zip file {} in zip dir {}",zipname,ZIP_DIR);
		zipCompress(ZIP_DIR, fileList, zipname);
		
		return isSuccess;
	}

	private static String parse(String srcFile) {

		int location = srcFile.lastIndexOf("/");
		if (location != -1) {
			return srcFile.substring(location + 1);
		}

		return srcFile;
	}

	private static String getFileName(String opCode, String messageType, int serialNumber) {

		StringBuilder sb = new StringBuilder();

		sb.append(Constant.APPLICATION_CODING).append(Constant.FILE_SPLIT_SYMBOL)

				.append(opCode).append(Constant.FILE_SPLIT_SYMBOL)

				.append(messageType).append(Constant.FILE_SPLIT_SYMBOL)

				.append(Constant.ADMINISTRATIVE_NUMBER).append(new Date().getTime()).append(Constant.FILE_SPLIT_SYMBOL)

				.append(String.format("%04d", serialNumber)).append(Constant.FILE_SPLIT_SYMBOL)

				.append("V2").append(Constant.FILE_EXT);

		return sb.toString();

	}

	public static void main(String[] args) {

		// // 得到文件名
		// System.out.println(getFileName(Constant.ACCOUNT, Constant.WA_RESULT, 22));
		//
		// System.out.println(FileUtil.getTotalXmlFile());
		//
		// // //压缩文件
		//
		// System.out.println(20 * 1024 * 1024);
		// zipCompressInBatch(FileUtil.getTotalXmlFile());
		
		zipCompressDir("/Users/nali/Desktop/test/","test.zip");
		System.out.println("---the end----");
	}

}
