package com.ximalaya.wa.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.ximalaya.wa.model.xml.Data;
import com.ximalaya.wa.model.xml.DataSet;
import com.ximalaya.wa.model.xml.Item;
import com.ximalaya.wa.model.xml.ReportResult;

public class Test {

	public static void main(String[] args) throws FileNotFoundException {
		
		String filePath = "/Users/nali/Desktop/zip/tmp1/1390008_ACCOUNT_3_3101161527618576836_3470_V2.xml";
		InputStream stream = new FileInputStream(new File(filePath));
		ReportResult result = OxmUtil.fromXML(ReportResult.class,stream); 
		
		DataSet report = result.getReport();
		List<Data> datas = report.getDatas();
		
		List<String> imgs = new ArrayList<String>();
		
		for (Data data : datas) {
			
			List<Item> items = data.getItems();
			
			for (Item item : items) {
				if (item.getKey().equals("USERPHOTO")) {
					imgs.add(item.getVal());
					File file = new File("/Users/nali/Desktop/zip/1390008_ACCOUNT_3_3101161527618576836_3470_V2/" + item.getVal());
					file.renameTo(new File("/Users/nali/Desktop/zip/tmp1/" + item.getVal()));
					
				}
			}
		}
		
		System.out.println(imgs);
		
	}
	
}
