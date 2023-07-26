package com.ximalaya.wa.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.ximalaya.wa.model.xml.Condition;
import com.ximalaya.wa.model.xml.Data;
import com.ximalaya.wa.model.xml.DataSet;
import com.ximalaya.wa.model.xml.Item;
import com.ximalaya.wa.model.xml.QueryRequest;

public class OxmUtil {

	private static XStream stream = new XStream();
	private static final Logger LOG = LoggerFactory.getLogger(OxmUtil.class);

	static {
		stream.processAnnotations(new Class[]{Data.class,Item.class,Condition.class,DataSet.class});
	}

	public static void toXML(Object xmlObject, File file) throws IOException {

		if (file == null||xmlObject==null)
			return;
		toXML(xmlObject, new FileOutputStream(file));

	}
	
	public static Object fromXML(String xml)  {
		stream.processAnnotations(QueryRequest.class);
		return stream.fromXML(xml);
	}
	public static <T>  T  fromXML(Class<T> type,InputStream inputStream)  {
		stream.processAnnotations(type);
		return (T) stream.fromXML(inputStream);
	}
	public static void toXML(Object xmlObject, OutputStream outputStream) {
		stream.processAnnotations(xmlObject.getClass());
		stream.toXML(xmlObject, outputStream);
	}
	
	
	public static void main(String[] args) {
		stream.processAnnotations(QueryRequest.class);
//		QueryRequest request = (QueryRequest) stream.fromXML(new File("/Users/nali/Desktop/query.xml"));
//		List<DataSet> dataSets = request.getDataSets();
//		Item item = dataSets.get(0).getDatas().get(0).getItems().get(1);
//		System.out.println(item.getKey());
//		System.out.println(item.getVal());
//		
		String opCode = "";
		Map<String,String> criteria = new HashMap<>();
		
		QueryRequest request = (QueryRequest) stream.fromXML(new File("/Users/nali/Desktop/query.xml"));
		List<DataSet> dataSets = request.getDataSets();
		for (DataSet dataSet : dataSets) {
			if (dataSet.getName().trim().equals("WA_COMMON_010000")) {
				Data data = dataSet.getDatas().get(0);
				List<Item> items = data.getItems();
				for (Item item : items) {
					if (item.getKey().trim().equals("OPCODE")) {
						opCode = item.getVal();
					}
				}
			}
			if (dataSet.getName().trim().equals("WA_COMMON_010010")) {
				Data data = dataSet.getDatas().get(0);
				Condition condition = data.getCondition();
				List<Item> items = condition.getItems();
				for (Item item : items) {
					criteria.put(item.getKey(), item.getVal());
				}
			}
		}
		System.out.println(opCode);
		System.out.println(criteria);
	}
	
}
