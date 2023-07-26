package wa;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ximalaya.wa.dao.MonitorDao;
import com.ximalaya.wa.dao.QueryDao;
import com.ximalaya.wa.monitor.model.Condition;
import com.ximalaya.wa.query.model.QueryCondition;

@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:application-context-test.xml"})
public class TestQueryService {
	
//	@Autowired
//	private RemotePassportService.Iface passportService;

//	@Autowired
//	private IWAQueryService waQueryService;
	
	@Autowired
	private QueryDao queryDao;
	
	@Autowired
	private MonitorDao monitorDao;
	
	@Test
	public void testInsert(){
		
		QueryCondition condition = new QueryCondition();
		condition.setOpcode("QUERYACCOUNT");
		condition.setCriteria("{\"USER_ACCOUNT\":\"11\"}");
		condition.setCreatedAt(new Date());
		queryDao.insert(condition);
		
//		Condition condition = new Condition();
//		condition.setConditionKey("USER_ACCOUNT");
//		condition.setConditionValue("111111");
//		condition.setOpcode("MONITORMEDIABROWSELOG");
//		condition.setPmId("111111111");
//		condition.setCreateAt(new Date());
//		monitorDao.insert(condition);
		
	}
	
	
//	@Test
//	public void testQuery(){
//		Map<String,String> map = new HashMap<>();
//		map.put(Constant.DATA_USERID, "1");
//		System.out.println(map.size());
//		waQueryService.query(Constant.QUERY_COMMENT, "11111",map);
//	}
//	
//	@Test
//	public void testPassport() throws TException{
//		
//		Passport passport = passportService.queryPassportByEmail("sys@ximalaya.com");
//		System.out.println(passport.getUid());
//		Passport passport2 = passportService.queryByMobile("13611111112");
//		System.out.println(passport2.getUid());
//		System.out.println(passport2.getPassword());
//		System.out.println(passport2.getMPhone());
//		
//	}
//	
//	@Test
//	public void testPutData2XML(){
//		//es不可能用空的条件去查询
//		//返回数据为空
//		Map<String,String> criteria = new HashMap<>();
//		
//		// QUERY_ACCOUNT
////		criteria.put(Constant.DATA_USER_INTENRALID, "1");
////		criteria.put(Constant.DATA_IP, "183.46.193.30");
////		criteria.put(Constant.DATA_USER_ACCOUNT, "liu_xiaoxia@china.com.cn");
////		criteria.put(Constant.DATA_MAC, "FCF75E9C-A2F4-4CFA-9D74-02C41758363B");
////		waQueryService.query(Constant.QUERY_ACCOUNT, criteria);
//		
//		//QUERYUCFORUMLOG
////		criteria.put(Constant.DATA_USER_INTENRALID, "65867510");
//
//		//QUERYMEDIABROWSELOG   					 80217226
//		criteria.put(Constant.DATA_USER_INTENRALID, "80217226");
//		waQueryService.query(Constant.QUERY_MEDIA_BROWSE_LOG, "111111", criteria);
//
//		
////		//QUERYRECHARGEWITHDRAWALS
////		waQueryService.query(Constant.QUERY_RECHARGE_WITH_DRAWALS, criteria);
////			
////		//QUERYREWARD
////		waQueryService.query(Constant.QUERY_REWARD, criteria);
////		
////		//QUERYPAYMENT
////		waQueryService.query(Constant.QUERY_PAYMENT, criteria);
////		
////		//QUERYPAYMENTORDER
////		waQueryService.query(Constant.QUERY_PAYMENT_ORDER, criteria);
//		
//	}
//	
//	@Test
//	public void testPlay(){
//		Map<String,String> criteria = new HashMap<>();
//		criteria.put(Constant.DATA_ACCOUNTNAME, "80217226");
//		criteria.put(Constant.DATA_BEGINTIME, "1498060800");
//		criteria.put(Constant.DATA_ENDTIME, "1498147200");
//		waQueryService.query(Constant.QUERY_MEDIA_BROWSE_LOG, "111111", criteria);
//	}
//	
//	@Test
//	public void testAccount(){
//		// QUERY_ACCOUNT
//		Map<String,String> criteria = new HashMap<>();
//		criteria.put(Constant.DATA_USER_INTENRALID, "1300370");
////		criteria.put(Constant.DATA_IP, "183.46.193.30");
////		criteria.put(Constant.DATA_USER_ACCOUNT, "liu_xiaoxia@china.com.cn");
////		criteria.put(Constant.DATA_MAC, "FCF75E9C-A2F4-4CFA-9D74-02C41758363B");
//		waQueryService.query(Constant.QUERY_ACCOUNT, "111111", criteria);
//	}
//	
//	@Test
//	public void testRelation(){
//		//QUERYRELATIONACCOUNTINFO
//		Map<String,String> criteria = new HashMap<>();
//		criteria.put(Constant.DATA_ACCOUNTNAME, "18964929");
//		waQueryService.query(Constant.QUERY_RELATION_ACCOUNT_INFO, "11111111", criteria);
//	}
//	
//	@Test
//	public void testQueryPlay(){
//		Map<String,String> criteria = new HashMap<>();
////		criteria.put(Constant.DATA_USER_INTENRALID, "80217226");
//		criteria.put(Constant.DATA_USER_ACCOUNT, "79419599");
//		waQueryService.query(Constant.QUERY_MEDIA_BROWSE_LOG, "111111", criteria);
//	}
//	
//	@Test
//	public void testQueryComment(){
//		Map<String,String> criteria = new HashMap<>();
//		criteria.put(Constant.DATA_USER_INTENRALID, "3877375");
//		waQueryService.query(Constant.QUERY_COMMENT, "111111", criteria);
//	}
//	
//	@Test
//	public void testQueryReward() throws JSchException{
//		
//		Map<String,String> criteria = new HashMap<>();
////		criteria.put(Constant.DATA_REWARD_USERID, "76501027");
////		criteria.put(Constant.DATA_RECEIVING_USERID, "85113616");
//		criteria.put(Constant.DATA_GOODS_NAME, "棒棒糖");
//		waQueryService.query(Constant.QUERY_REWARD, "111111", criteria);
//	}
//	
//	@Test
//	public void testLogin1(){
//		Map<String,String> criteria = new HashMap<>();
//		criteria.put(Constant.DATA_USER_INTENRALID, "82140179");
//		waQueryService.query(Constant.QUERY_UCFORUM_LOG, "111111", criteria);
//	}
//	
//	@Test
//	public void testLogin2(){
//		Map<String,String> criteria = new HashMap<>();
//		criteria.put(Constant.DATA_USER_INTENRALID, "65245591");
//		criteria.put(Constant.DATA_BEGINTIME, "1498492800");
//		criteria.put(Constant.DATA_ENDTIME, "1498579200");
//		waQueryService.query(Constant.QUERY_UCFORUM_LOG, "111111", criteria);
//	}
//	
//	@Test
//	public void testPaymentOrder(){
//		Map<String,String> criteria = new HashMap<>();
//		criteria.put(Constant.DATA_ORDERNUM, "2016052402005726623623");
//		waQueryService.query(Constant.QUERY_PAYMENT_ORDER,"111111",  criteria);
//	}
//	
//	@Test
//	public void testn(){
//		System.out.println(ValuesHolder.getValue("${query.dir}"));
//	}
}
