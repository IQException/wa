//package wa;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.util.List;
//
//import com.ximalaya.wa.config.Dict;
//import com.ximalaya.wa.config.Keys;
//import com.ximalaya.wa.model.xml.Data;
//import com.ximalaya.wa.model.xml.DataSet;
//import com.ximalaya.wa.model.xml.Item;
//import com.ximalaya.wa.model.xml.ReportResult;
//import com.ximalaya.wa.util.OxmUtil;
//
//public class WAXmlParseUtil {
//
//	public static String parse(String content) {
//
//		String str = "";
//		OxmUtil.fromXML(ReportResult.class,new InputStream(new FileInputStream(new File(str))));
//		ReportResult result = (ReportResult) OxmUtil.fromXML(content); // parse xml content
//		DataSet common = result.getCommon();
//		DataSet report = result.getReport();
//		
//		if (dataSet1.getName().trim().equals(Dict.DS_COMMOM)) {
//			
//			Data data = dataSet1.getDatas().get(0);
//			
//			List<Item> items = data.getItems();
//			
//			for (Item item : items) {
//				if (item.getKey().trim().equals(Keys.MSGID)) {
//					String msgId = item.getVal();
//				}
//			}
//			
//		}  // opCode,msgId,opId
//		
//		
//		// query,monitor,manage
//		if (dataSets.size() > 1) {
//			DataSet dataSet2 = dataSets.get(1);
//			
//		}
//		
//		return str;
//		
//	}
//
//	public static void main(String[] args) {
//		
//		
//	}
//
//}
