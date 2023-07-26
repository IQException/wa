package com.ximalaya.wa.helper;

import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

@Component
public class ValuesHolder implements EmbeddedValueResolverAware {

	private static StringValueResolver valueResolver;

	public static String getValue(String strVal){
		
		return valueResolver.resolveStringValue(strVal);
		
	}

	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		valueResolver = resolver;		
	}
	
}