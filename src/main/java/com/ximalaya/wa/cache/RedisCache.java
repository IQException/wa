package com.ximalaya.wa.cache;

import java.util.Map;

import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.ximalaya.mobile.common.SampleLogger;
import com.ximalaya.wa.helper.BeansHolder;

public class RedisCache {
	
	private static final Logger logger = LoggerFactory.getLogger(RedisCache.class);
    protected static final SampleLogger sampleLogger = SampleLogger.getLogger(RedisCache.class,30);

	private static StringRedisTemplate redisTemplate = (StringRedisTemplate) BeansHolder.getBean("stringRedisTemplate");
	private static StringRedisTemplate codisTemplate = (StringRedisTemplate) BeansHolder.getBean("stringByteCodisTemplate");

	private static final String FILESTAT = "wa:filestat:recordnum";
	private static final String DATA_TRACK = "data:track:";
	
	static LoadingCache<String, String> cacheFileMd5 = CacheBuilder.newBuilder().maximumSize(10000).concurrencyLevel(5)
			.build(new CacheLoader<String, String>() {

				@Override
				public String load(String trackId) throws Exception {
					return getMd5ByTrackIdFromRedis(trackId);
				}
				
				private String getMd5ByTrackIdFromRedis(String trackId) {
					String key = DATA_TRACK + trackId;
					String fileMd5 = String.valueOf(codisTemplate.opsForHash().get(key, "md5"));
					if (fileMd5.equals("null")) {
						return null;
					}
					return fileMd5;
				}

			});
	
	static LoadingCache<String, String>	cacheFileSize = CacheBuilder.newBuilder().maximumSize(50000).concurrencyLevel(5)
		     .build(new CacheLoader<String, String>() {

				@Override
				public String load(String trackId) throws Exception {
					return getFileSizeByTrackIdFromRedis(trackId);
				}
				
				public String getFileSizeByTrackIdFromRedis(String trackId) {
					String key = DATA_TRACK + trackId;
					byte[] value = (byte[]) codisTemplate.opsForHash().get(key, "file_size");
					String fileSize = String.valueOf(Bytes.toLong(value));
					return fileSize;
				}

				

		     });
	
	static LoadingCache<String, String>	cacheFileFormat = CacheBuilder.newBuilder().maximumSize(5000).concurrencyLevel(5)
		     .build(new CacheLoader<String, String>() {

				@Override
				public String load(String trackId) throws Exception {
					return getFileFormatByTrackIdFromRedis(trackId);
				}
				
				public String getFileFormatByTrackIdFromRedis(String trackId) {
					String key = DATA_TRACK + trackId;
					String fileFormat = String.valueOf(codisTemplate.opsForHash().get(key, "file_format"));
					if (fileFormat.equals("null")) {
						return null;
					}
					return fileFormat;
				}

		     });
	
	
	public static String getMd5ByTrackId(String trackId){
		try {
			return cacheFileMd5.get(trackId);
		} catch (Exception e) {
//			logger.error("guava cache for md5 get error,trackId : {}",trackId,e);
		}
		return null;
	}

	public static String getFileSizeByTrackId(String trackId) {
		try {
			return cacheFileSize.get(trackId);
		} catch (Exception e) {
//			logger.error("guava cache for file size get error,trackId : {}",trackId,e);
		}
		return null;
	}

	public static String getFileFormatByTrackId(String trackId) {
		try {
			return cacheFileFormat.get(trackId);
		} catch (Exception e) {
//			logger.error("guava cache for file format get error,trackId : {}",trackId,e);
		}
		return null;
	}

	public static final Map<String, Integer> filestat = Maps.newConcurrentMap();

	public static int getRecordNum(String fileName) {
		int num = Integer.parseInt(String.valueOf(redisTemplate.opsForHash().get(FILESTAT, fileName)));
		redisTemplate.opsForHash().delete(FILESTAT, fileName);
		return num;
	}

	public static void setRecordNum(String fileName, int recordNum) {
		redisTemplate.opsForHash().put(FILESTAT, fileName, String.valueOf(recordNum));
	}

}
