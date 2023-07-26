package com.ximalaya.wa.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.derby.tools.sysinfo;

import com.alibaba.fastjson.util.Base64;
import com.ximalaya.dtres.util.FilePathUtils;
import com.ximalaya.wa.model.xml.Data;
import com.ximalaya.wa.model.xml.Item;

import jodd.util.StringUtil;

public class DataUtil {

	public static List<Data> getResonseData(String status, String errorCode) {

		List<Data> list = new ArrayList<>();

		Data data = new Data();

		List<Item> items = new ArrayList<>();
		Item item1 = new Item();
		item1.setKey("STATUS");
		item1.setVal(status);
		items.add(item1);

		// 绝对秒数
		Item item2 = new Item();
		item2.setKey("TIMESTAMP");
		long time = new Date().getTime();
		String value = String.valueOf(time).substring(0, 10);
		item2.setVal(value);
		items.add(item2);

		Item item3 = new Item();
		item3.setKey("ERRORCODE");
		item3.setVal(errorCode);
		items.add(item3);

		data.setItems(items);

		list.add(data);

		return list;
	}

	public static List<Data> getQueryNodeStatusResonseData() {

		List<Data> list = new ArrayList<>();

		Data data = new Data();

		List<Item> items = new ArrayList<>();
		Item item1 = new Item();
		item1.setKey("MSGSTATUS");
		item1.setVal("0");
		items.add(item1);

		// 绝对秒数
		Item item2 = new Item();
		item2.setKey("MSGTIME");
		long time = new Date().getTime();
		String value = String.valueOf(time).substring(0, 10);
		item2.setVal(value);
		items.add(item2);

		Item item3 = new Item();
		item3.setKey("ERRORCODE");
		item3.setVal("0");
		items.add(item3);

		data.setItems(items);

		list.add(data);

		return list;
	}

	public static List<Data> getMonitorResultData(String account, String uid, long monitorTime, boolean status) {

		List<Data> list = new ArrayList<>();
		Data data = new Data();

		List<Item> items = new ArrayList<>();

		if (account != null) {
			Item itemAccount = new Item();
			itemAccount.setKey("ACCOUNT");
			itemAccount.setVal(account);
			items.add(itemAccount);
		}

		Item itemUid = new Item();
		itemUid.setKey("INTENRALID");
		itemUid.setVal(uid);
		items.add(itemUid);

		// 绝对秒数
		Item itemTime = new Item();
		itemTime.setKey("MONITORTIME");
		String time = String.valueOf(monitorTime);
		if (time.length() < 4) {
			itemTime.setVal("0");
		} else {
			String value = time.substring(0, time.length() - 3);
			itemTime.setVal(value);
		}
		items.add(itemTime);

		Item itemStatus = new Item();
		itemStatus.setKey("MONITORTSTATUS");
		if (status) {
			itemStatus.setVal("1");
		} else {
			itemStatus.setVal("2");
		}
		items.add(itemStatus);

		data.setItems(items);
		list.add(data);

		return list;
	}
	
	public static List<Data> getMonitorResultData(Map<String,String> criteria, long monitorTime, boolean status) {

		List<Data> list = new ArrayList<>();
		Data data = new Data();

		List<Item> items = new ArrayList<>();

		for (Map.Entry<String, String> entry : criteria.entrySet()) {
			Item item = new Item();
			item.setKey(entry.getKey());
			item.setVal(entry.getValue());
			items.add(item);
		}

		// 绝对秒数
		Item itemTime = new Item();
		itemTime.setKey("MONITORTIME");
		String time = String.valueOf(monitorTime);
		if (time.length() < 4) {
			itemTime.setVal("0");
		} else {
			String value = time.substring(0, time.length() - 3);
			itemTime.setVal(value);
		}
		items.add(itemTime);

		Item itemStatus = new Item();
		itemStatus.setKey("MONITORTSTATUS");
		if (status) {
			itemStatus.setVal("1");
		} else {
			itemStatus.setVal("2");
		}
		items.add(itemStatus);

		data.setItems(items);
		list.add(data);

		return list;
	}


	public static List<Data> getManageResultData(String errorCode, String failReason, String remark) {

		List<Data> list = new ArrayList<>();
		Data data = new Data();

		List<Item> items = new ArrayList<>();

		Item item1 = new Item();
		item1.setKey("ERRORCODE");
		item1.setVal(errorCode);
		items.add(item1);

		Item item2 = new Item();
		item2.setKey("FAIL_REASON");
		item2.setVal(failReason);
		items.add(item2);

		Item item3 = new Item();
		item3.setKey("REMARK");
		item3.setVal(remark);
		items.add(item3);

		data.setItems(items);

		list.add(data);

		return list;
	}

	public static void main(String[] args) throws ParseException {
		
		String filePath = "/Users/nali/Desktop/report.sh";
		String destPath = "/Users/nali/Desktop/tmp/report.sh";
		File file = new File(filePath);
		
		File destFile = new File(destPath);
		file.renameTo(destFile);
		 

	}
}
