package wa;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ximalaya.wa.converter.WaConverter;
import com.ximalaya.wa.dao.MonitorDao;
import com.ximalaya.wa.model.xml.Data;
import com.ximalaya.wa.model.xml.ResponseInfo;
import com.ximalaya.wa.monitor.model.Condition;
import com.ximalaya.wa.sender.model.Constant;
import com.ximalaya.wa.sender.util.FileUtil;
import com.ximalaya.wa.service.IWAMonitorService;
import com.ximalaya.wa.util.DataUtil;
import com.ximalaya.wa.util.OxmUtil;


@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:application-context.xml"})
public class TestMonitorService {

	@Autowired
	private IWAMonitorService waMonitorService;
	
	@Autowired
	private MonitorDao dao;
	
	@Test
	public void testADD() {
		
		Map<String,String> criteria = new HashMap<>();
		criteria.put("USER_ACCOUNT", "111111");
		
		waMonitorService.monitor("ADDMONITORMEDIABROWSELOG", criteria, "1502071881678568211500155", "111111111");
		
	}
	
	@Test
	public void testDEL() {
		
		Map<String,String> criteria = new HashMap<>();
		criteria.put("USER_ACCOUNT", "111111");
		
		waMonitorService.monitor("DELMONITORMEDIABROWSELOG", criteria, "1502071881678568211500155", "2222222222");
		
	}
	
	@Test
	public void testSTATUS() {
		
		Map<String,String> criteria = new HashMap<>();
		criteria.put("USER_ACCOUNT", "111111");
		
		waMonitorService.monitor("ADDMONITORMEDIABROWSELOGSTATUS", criteria, "1502071881678568211500155", "111111111");
		
	}
	
	@Test
	public void test1(){
		Condition condition = new Condition();
		condition.setConditionKey("USER_ACCOUNT");
		condition.setConditionValue("111111");
		condition.setOpcode("MONITORMEDIABROWSELOG");
		condition.setPmId("111111111");
		condition.setCreateAt(new Date());
		dao.insert(condition);
	}
	
	@Test
	public void test2(){
		Condition condition = dao.selectByCriteria("MONITORMEDIABROWSELOG", "USER_ACCOUNT", "111111");
		System.out.println(condition.getOpcode());
	}

	@Test
	public void test3(){
		Condition condition = new Condition();
		condition.setOpcode("MONITORMEDIABROWSELOG");
		condition.setConditionKey("USER_ACCOUNT");
		condition.setConditionValue("111111");
		dao.delete(condition);
	}
	
	@Test
	public void test4(){
		String dir = FileUtil.getMonitorXMLDir("DELMONITORMEDIABROWSELOG", Constant.WA_RESPONSE,"111111111111");
		List<Data> status = DataUtil.getResonseData("0", "0");
		try {
			ResponseInfo response = WaConverter.convertToCommonResponse(status, null, "DELMONITORMEDIABROWSELOG", "1502071881678568211500155", "111111");
			OxmUtil.toXML(response.getResponse(), new File(dir));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
