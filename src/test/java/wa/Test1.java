package wa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test1 {
	
	public static void main(String[] args) throws IOException {
//		// 拿到所有的zip文件名，写入脚本，解压
//		String filePath = "/Users/nali/Desktop/test/";
//		String destPath = "/Users/nali/Desktop/wa-account/";
//		File file = new File(filePath);
//		String[] fileArr = file.list();
//
//		StringBuilder sb = new StringBuilder();
//
//		sb.append("cd " + filePath).append("\n");
//
//		for (String str : fileArr) {
//			if (isZipFile(str)) {
//				String dir = str.substring(0, str.length() - 4);
//				sb.append("mkdir " + dir).append("\n");
//				sb.append("unzip " + str + " -d " + destPath + dir).append("\n");
//			}
//		}
//
//		System.out.println(sb.toString());
		File file = new File("/Users/nali/git/wa/src/test/java/wa/myfile/myfile");
		FileOutputStream os = new FileOutputStream(file);
		os.write("test".getBytes());

	}

	private static boolean isZipFile(String filePath) {
		Pattern pattern = Pattern.compile(".*\\.zip$");
		Matcher matcher = pattern.matcher(filePath);
		return matcher.matches();
	}

}
