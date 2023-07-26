package com.ximalaya.wa.helper;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class BeansHolder implements ApplicationContextAware {

	private static ApplicationContext beansHolder;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		beansHolder = applicationContext;
	}
	
	public static Object getBean(String beanName){
		
		return beansHolder.getBean(beanName);
		
	}

	public static <T> T getBean(Class<T> clazz){
		
		return beansHolder.getBean(clazz);
		
	}
	

}