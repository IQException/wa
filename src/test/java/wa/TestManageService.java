package wa;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jcraft.jsch.JSchException;
import com.ximalaya.wa.converter.WaConverter;
import com.ximalaya.wa.model.xml.Data;
import com.ximalaya.wa.model.xml.ResponseInfo;
import com.ximalaya.wa.sender.model.Constant;
import com.ximalaya.wa.sender.util.FileUtil;
import com.ximalaya.wa.sender.util.SFTPUploadUtil;
import com.ximalaya.wa.service.IWAManageService;
import com.ximalaya.wa.util.DataUtil;
import com.ximalaya.wa.util.OxmUtil;
import com.ximalaya.xmsms.inf.api.BaseResult;
import com.ximalaya.xmsms.inf.api.SendMessageService;

@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = { "classpath:application-context.xml" })
public class TestManageService {

	@Autowired
	private IWAManageService manageService;
	
	@Autowired
	private SendMessageService.Iface sendMessageService;
	
//	@Autowired
//	private ManageDao manageDao;
//	
//	@Test
//	public void query(){
//		List<WaManage> list = manageDao.loadAllNotFinished();
//		for (WaManage waManage : list) {
//			System.out.println(waManage.getManageValue());
//		}
//	}
	
//	@Test
//	public void insert(){
//		WaManage waManage = new WaManage();
//		waManage.setCreateAt(new Date());
////		waManage.setEndTime(new Date().getTime());
//		waManage.setStartTime(1456678696L);
//		waManage.setManageKey("USER_ID");
//		waManage.setManageValue("1234566");
//		waManage.setParamKey("0701_1");
//		manageDao.insert(waManage);
//	}
	
//	@Test
//	public void update(){
//		WaManage waManage = new WaManage();
//		waManage.setId(1L);
//		waManage.setStatus(1);
//		manageDao.update(waManage);
//	}
	
	@Test
	public void testSms(){
		try {
			BaseResult result = sendMessageService.sendRemindMsg("18501792408", "喜马拉雅系统说，你是这个世界上最帅的！", "1");
			System.out.println(result);
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void test() {
		Map<String, String> criteria = new HashMap<>();
		criteria.put("USER_ACCOUNT", "18501792408");
		criteria.put("BEGINTIME", "1508295492");
		criteria.put("ENDTIME", "1508342400");
		criteria.put("PARAM_NAME_0601", "0");
		criteria.put("PARAM_NAME_0611", "0");
		manageService.manage("FREEZEUSERS", criteria, "010000150284938389993", "824116231111");
	}
	
	@Test
	public void testMessage() {
		Map<String, String> criteria = new HashMap<>();
		criteria.put("URL", "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3144465310,4114570573&fm=27&gp=0.jpg");
		criteria.put("BEGINTIME", "1508295492");
		criteria.put("ENDTIME", "1508342400");
		criteria.put("PARAM_NAME_0707", "3");
		criteria.put("PARAM_NAME_0705", "0");
		manageService.manage("FREEZEMESSAGES", criteria, "010000150284938389993", "824116231111");
	}
	
	@Test
	public void testIp() {
		Map<String, String> criteria = new HashMap<>();
		criteria.put("START_IP", "192.168.3.1");
		criteria.put("END_IP", "192.168.3.60");
		criteria.put("PARAM_NAME_0403", "0");
		criteria.put("PARAM_NAME_0407", "0");
		criteria.put("BEGINTIME", "1508295492");
		criteria.put("ENDTIME", "1508342400");
		manageService.manage("FREEZEAREAS", criteria, "010000150284938389994", "824116231113");
	}

	@Test
	public void test2() throws JSchException {
		String dir = "/Users/nali/Desktop/tmp/manage/";
		String filePath = "1390008_FREEZEIPZONE_2_1502071881678568211500155_2681_V2.xml";
		SFTPUploadUtil.uploadFile(SFTPUploadUtil.JINGKOU, dir + filePath);
	}
	

	@Test
	public void test3() throws Exception {
		List<Data> status = DataUtil.getQueryNodeStatusResonseData();
		String dir = FileUtil.getQueryXMLDir(Constant.QUERY_NODE_STATUS, Constant.WA_RESPONSE, "0100001502420266020114");
		ResponseInfo response = WaConverter.convertToCommonResponse(status, null, Constant.QUERY_NODE_STATUS, "0100001502420266020114", "1111111");
		OxmUtil.toXML(response.getResponse(), new File(dir));
	}
	

	@Test
	public void test4() {
		String dir = "/Users/nali/Desktop/tmp/bak";

		File file = new File(dir);

		List<File> files = new ArrayList<>();

		for (File dateFile : file.listFiles()) {
			files.add(dateFile);
		}

//		Collections.sort(files);

		while (files.size() > 14) {
			FileUtil.deleteDir(files.remove(0));
		}
	}
	
	@Test
	public void test5(){
		
		Map<String, String> criteria = new HashMap<>();
		criteria.put("START_IP", "192.168.73.127");
		criteria.put("END_IP", "192.168.73.127");
		criteria.put("BEGINTIME", "1502323200");
		criteria.put("ENDTIME", "1507564800");
		criteria.put("PARAM_NAME_0405","0");
		
		manageService.manage("FREEZEAREAS", criteria, "0100001502420266020114", "1111111");
		
	}

}
