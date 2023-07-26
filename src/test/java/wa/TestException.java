package wa;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import com.ximalaya.wa.model.Login;

public class TestException {

	public static void main(String[] args) throws IntrospectionException {

	    for (PropertyDescriptor pd :Introspector.getBeanInfo(Login.class).getPropertyDescriptors())
	    {
	        System.err.println(pd.getName()+":"+pd.getDisplayName());
	    }
	}

}
