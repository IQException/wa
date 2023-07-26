package wa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:application-context.xml"})
public class TestFullAmountSearch {
	
//	@Autowired
//	private AccountFullAmountUpload fullAmountService;
	
	@Test
	public void test() throws Exception{
//		fullAmountService.startAccountFullAmountUploadTask();
	}

}
