package com.ximalaya.wa.cache;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.nali.common.util.CollectionUtils;
import com.ximalaya.wa.util.ElasticSearchUtil;

/**
 * 
 * @author nali 缓存uid和account的map
 *
 */

public class GuavaCache {
	
	private static final Logger logger = LoggerFactory.getLogger(GuavaCache.class);

	static LoadingCache<String, String>	cache = CacheBuilder.newBuilder().maximumSize(10000000).concurrencyLevel(5)
		     .build(new CacheLoader<String, String>() {

				@Override
				public String load(String uid) throws Exception {
					return getAccountByUidFromEs(uid);
				}
				
				private String getAccountByUidFromEs(String uid){
					return ElasticSearchUtil.getAccountByUid(uid);
				}

		     });
	
	public static List<String> removeExistUid(List<String> uids){
		if (CollectionUtils.isEmpty(uids)) {
			return null;
		}
		
		Iterator<String> iterator = uids.iterator();
		while(iterator.hasNext()){
			if (cache.asMap().containsKey(iterator.next())) {
				iterator.remove();
			}
		}
		return uids;
	}
	
	
	public static void putData(Map<String,String> map){
		
		if (map != null && map.size() > 0) {
			Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
			Entry<String, String> entry = (Entry<String, String>) iterator.next();
			if (!cache.asMap().containsKey(entry.getKey())) {
				cache.put(entry.getKey(),entry.getValue());
			}
		}
		
	}
	
	public static String getAccountByUid(String uid) {
		try {
			return cache.get(uid);
		} catch (Exception e) {
			logger.error("guava cache get error,uid : {}",uid);
		}
		return null;
	}
	
	public static void main(String[] args) throws ExecutionException {
		
		System.out.println(getAccountByUid("1438738"));
		System.out.println(cache.size());
		System.out.println(getAccountByUid("1650635"));
		System.out.println(cache.size());
		System.out.println(getAccountByUid("1097208"));
		System.out.println(cache.size());
		System.out.println(getAccountByUid("128"));
		System.out.println(cache.size());
		
	}
	
	
}
