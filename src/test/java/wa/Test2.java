package wa;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test2 {
	
	public static final String	AREA_SH	= "310116";

	public static void main(String[] args) {
		
		String filePath = "/Users/nali/Desktop/wa-account/";
		
		File file = new File(filePath);
		
		String[] fileArr = file.list();
		
		for (String dir : fileArr) {
			if (isDir(dir)) {
				
				// 解析xml文件
				String xmlPath = dir + "/" + dir + ".xml";
//				System.out.println(filePath + xmlPath);
				
				
				
				
				
				// 拿到表头，更改表头中的msgId
				// 拿到数据，分成3组，并拿到每一组的USERPHOTO数据
				// 重新命名文件夹
				// 生成xml文件，并且拿到相应的图片
				// 压缩
				// 上传
				
				
				
				
				
				
				
				
				
				
			}
		}
		
	}
	
	private static boolean isDir(String filePath) {
		Pattern pattern = Pattern.compile(".*\\_V2$");
		Matcher matcher = pattern.matcher(filePath);
		return matcher.matches();
	}
	
	private static String genSeq() {
        return AREA_SH + System.currentTimeMillis();
    }
	
}
