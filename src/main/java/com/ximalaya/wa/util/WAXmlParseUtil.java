package com.ximalaya.wa.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.util.Base64;
import com.ximalaya.dtres.util.FilePathUtils;
import com.ximalaya.wa.config.Dict;
import com.ximalaya.wa.config.Keys;
import com.ximalaya.wa.model.xml.Condition;
import com.ximalaya.wa.model.xml.Data;
import com.ximalaya.wa.model.xml.DataSet;
import com.ximalaya.wa.model.xml.Item;
import com.ximalaya.wa.model.xml.QueryRequest;
import com.ximalaya.wa.sender.model.Constant;

public class WAXmlParseUtil {

	public static final String	OPCODE		= "opCode";
	public static final String	CRITERIA	= "criteria";
	public static final String	TYPE		= "type";
	public static final String	MSGID		= "msgId";
	public static final String	OPID		= "opId";

	/**
	 * 
	 * @param content
	 * @return 拿去网安的请求条件
	 * 
	 */
	public static Map<String, Object> parse(String content) {

		Map<String, Object> map = new HashMap<>();

		String opCode = "";
		String opId = "";
		String msgId = "";
		Map<String, String> criteria = new HashMap<>();
		

		QueryRequest request = (QueryRequest) OxmUtil.fromXML(content);// parse xml content
		List<DataSet> dataSets = request.getDataSets();
		DataSet dataSet1 = dataSets.get(0); // request head, common
		
		if (dataSet1.getName().trim().equals(Dict.DS_COMMOM)) {
			
			Data data = dataSet1.getDatas().get(0);
			
			List<Item> items = data.getItems();
			
			for (Item item : items) {
				if (item.getKey().trim().equals(Keys.OPCODE)) {
					opCode = item.getVal();
				}
				if (item.getKey().trim().equals(Keys.OPID)) {
					opId = item.getVal();
				}
				if (item.getKey().trim().equals(Keys.MSGID)) {
					msgId = item.getVal();
				}
			}
			
		}  // opCode,msgId,opId
		
		
		map.put(OPCODE, opCode);
		map.put(MSGID, msgId);
		map.put(OPID, opId);
		
		
		if (opCode.equals(Constant.QUERY_NODE_STATUS)) {
			return map;
		}
		if (opCode.equals(Constant.COMM_CONTENT)) {
			DataSet dataSet2 = dataSets.get(1);
			Data data = dataSet2.getDatas().get(0);
			Condition condition = data.getCondition();
			List<Item> items = condition.getItems();
			for (Item item : items) {
				criteria.put(item.getKey(), new String(Base64.decodeFast(item.getVal())));
			}
			map.put(CRITERIA, criteria);
			return map;
		} // 联调性检查

		
		// query,monitor,manage
		if (dataSets.size() > 1) {
			DataSet dataSet2 = dataSets.get(1);
			
			if (dataSet2.getName().trim().equals(Dict.DS_QUERY)) {
				Data data = dataSet2.getDatas().get(0); // 只会有一个condition，但可能有多个item
				Condition condition = data.getCondition();
				List<Item> items = condition.getItems();
				for (Item item : items) {
					criteria.put(item.getKey(),item.getVal());
				}
				map.put(TYPE, Constant.QUERY_TYPE);
			} // query

			if (dataSet2.getName().trim().equals(Dict.DS_MONITOR)) {
				Data data = dataSet2.getDatas().get(0); // 只会有一个condition，但可能有多个item
				Condition condition = data.getCondition();
				List<Item> items = condition.getItems();
				for (Item item : items) {
					criteria.put(item.getKey(), item.getVal());
				}
				if (opCode.contains("MONITOR")) {
					map.put(TYPE, Constant.MONITOR_TYPE);
				}else{
					map.put(TYPE, Constant.MANAGEMENT_TYPE);
				}
			} // monitor,manage
		}
		
		map.put(CRITERIA, criteria);
		return map;
		
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		
//		String dir = "/Users/nali/Desktop/";
//		String fileName = "a.txt";
//		System.out.println(FilePathUtils.assembleLocalFilePath(dir,fileName));
		
		String content = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KCjxNRVNTQUdFPiAKICA8REFUQVNFVCBuYW1lPSJXQV9DT01NT05fMDEwMDAwIj4gCiAgICA8REFUQT4gCiAgICAgIDxJVEVNIGtleT0iQVBQVFlQRSIgdmFsPSIxMzkwMDA4Ii8+ICAKICAgICAgPElURU0ga2V5PSJPUENPREUiIHZhbD0iUVVFUllBQ0NPVU5UIi8+ICAKICAgICAgPElURU0ga2V5PSJNU0dJRCIgdmFsPSIxNTAxNjU1NjQyMjMyNjg3ODExNTAwMTU0Ii8+ICAKICAgICAgPElURU0ga2V5PSJNU0dUWVBFIiB2YWw9IjEiLz4gIAogICAgICA8SVRFTSBrZXk9Ik9QSUQiIHZhbD0iMTE1MzEwMDAwMTk1MDMiLz4gCiAgICA8L0RBVEE+IAogIDwvREFUQVNFVD4gIAogIDxEQVRBU0VUIG5hbWU9IldBX0NPTU1PTl8wMTAwMTAiPiAKICAgIDxEQVRBPiAKICAgICAgPENPTkRJVElPTj4gCiAgICAgICAgPElURU0ga2V5PSJVU0VSX0FDQ09VTlQiIHZhbD0iMTMwMDM3MCIvPgogICAgICA8L0NPTkRJVElPTj4gCiAgICA8L0RBVEE+IAogIDwvREFUQVNFVD4gCjwvTUVTU0FHRT4K";
		String content1 = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KCjxNRVNTQUdFPiAKICA8REFUQVNFVCBuYW1lPSJXQV9DT01NT05fMDEwMDAwIj4gCiAgICA8REFUQT4gCiAgICAgIDxJVEVNIGtleT0iQVBQVFlQRSIgdmFsPSIxMzkwMDA4Ii8+ICAKICAgICAgPElURU0ga2V5PSJPUENPREUiIHZhbD0iUVVFUllOT0RFU1RBVFVTIi8+ICAKICAgICAgPElURU0ga2V5PSJNU0dJRCIgdmFsPSIzMTAwMDAxNTAyMTc2MDQ5OTc2NTUiLz4gIAogICAgICA8SVRFTSBrZXk9Ik1TR1RZUEUiIHZhbD0iMSIvPiAgCiAgICAgIDxJVEVNIGtleT0iT1BJRCIgdmFsPSIxNTAyMTcwMjg4Nzc1Ii8+IAogICAgPC9EQVRBPiAKICA8L0RBVEFTRVQ+IAo8L01FU1NBR0U+Cg==";
		String content2 = "wCGNipy70WII3wmrspKL24hZ3J8usCMAw4o5wBQiAyMDE4MDUzMDIwMDQyMDAxMDAxMTAzNDE2OTYxMTAxNEgDUAA=";
//		System.out.println(parse(new String(Base64.decodeFast(content2))));
		System.out.println(new String(Base64.decodeFast(URLDecoder.decode(content2,"UTF-8")),"UTF-8"));

	}

}
