package com.ximalaya.wa.cache;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class ByteArraySerializer implements RedisSerializer<byte[]>{
	
	 private final static byte[] EMPTY_BYTE = new byte[]{0};

	  @Override
	  public byte[] deserialize(byte[] bytes) throws SerializationException {
	    if (bytes == null || bytes.length == 0) {
	      return EMPTY_BYTE;
	    }

	    return bytes;
	  }

	  @Override
	  public byte[] serialize(byte[] bytes) throws SerializationException {
	    return bytes;
	  }

}
