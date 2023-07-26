package com.ximalaya.wa.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jcraft.jsch.JSchException;
import com.nali.common.util.StringUtil;
import com.ximalaya.wa.sender.util.FileUtil;
import com.ximalaya.wa.sender.util.SFTPUploadUtil;
import com.ximalaya.wa.sender.util.ZipCompressUtil;


/**
 * 可以对临时目录下的xml文件进行处理
 * @author Stan.She
 *
 */
@Controller
public class FunctionController {
	
	@Value("${xml.tmp.dir}")
	private String			XML_STORE_DIR;
	
	@Value("${zip.dir}")
	private String			ZIP_DIR;
	
	private final Logger logger = LoggerFactory.getLogger(FunctionController.class);
	
	@RequestMapping("/function/zip_and_upload")
	@ResponseBody
	public String upload(@RequestParam String opcode){
		
		if (StringUtil.isEmpty(opcode)) {
			logger.info("parameter error ,opcode : {}",opcode);
			return  "{ret:1" + "\",message:\"" + opcode + " error ..\"}";
		}
		
		List<String> files = new ArrayList<>();
		
		logger.info("zip and upload task begin ...");
		
		File file = new File(XML_STORE_DIR);
		
		for (String opcodeFile : file.list()) {
			if (FileUtil.isXmlFile(opcodeFile) && opcodeFile.contains(opcode)) {
				files.add(XML_STORE_DIR + opcodeFile);
			}
		}
		
		if (CollectionUtils.isEmpty(files)) {
			logger.info("the xml for {} has no file ...",opcode);
			return  "{ret:1" + "\",message:\"" + opcode + " has no file ..\"}";
		}

		
		ZipCompressUtil.zipCompressInBatch(files, ZIP_DIR);

		// 3.上传文件
		try {
			SFTPUploadUtil.uploadFileInBatch(SFTPUploadUtil.REPORT, ZIP_DIR);
		} catch (JSchException e) {
			logger.error(e.getMessage(),e);
		}
		
		return "{ret:0" + ",message:\"SUCCESS,file size : "+ files.size() +"\"}";
	}

}
