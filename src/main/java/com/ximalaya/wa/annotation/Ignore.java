package com.ximalaya.wa.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * 忽略某个field：用于某些model继承了BaseModel，但是某些字段又不需要，只需要在子类同名field加上此注解即可
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Ignore {

}
