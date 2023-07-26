package com.ximalaya.wa.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapped {

	//网安上报字段名
	 String wa() ;
	//喜马拉雅业务字段名
	 String xm() default "";  
	//是否base64加密
	 boolean enc() default false;
}
