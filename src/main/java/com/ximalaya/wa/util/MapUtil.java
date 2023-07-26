package com.ximalaya.wa.util;

import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Splitter;

public class MapUtil {

    public static Object get(Map map, String naviString) {
        Iterator<String> fit = Splitter.on(".").omitEmptyStrings().trimResults().split(naviString).iterator();
        Object value = null;
        while (fit.hasNext()) {
            value = map.get(fit.next());
            if (value == null)
                return value;
            if (fit.hasNext()) {
                map = (Map) value;
            }
        }
        return value;
    }

    public static Object getFirstNotNull(Map map, String naviStrings) {
        Iterator<String> fit =
            Splitter.on(",").omitEmptyStrings().trimResults().omitEmptyStrings().split(naviStrings).iterator();
        Object value = null;

        while (fit.hasNext()) {
            value = get(map, fit.next());
            if (value != null)
                break;
        }
        return value;
    }

    public static Object getFirstNotNullAndNotEquals(Map map, String naviStrings, Object... comparators) {
        Iterator<String> fit =
            Splitter.on(",").omitEmptyStrings().trimResults().omitEmptyStrings().split(naviStrings).iterator();
        Object value = null;

        while (fit.hasNext()) {
            value = get(map, fit.next());
            if (value != null) {
                boolean notequals = true;

                if (comparators != null && comparators.length > 0) {
                    for (Object comparator : comparators) {
                        if (value.equals(comparator)){
                            notequals = false;
                            break;  
                        }
                    }
                }
                if(notequals) break;

            }
        }
        return value;
    }

}
