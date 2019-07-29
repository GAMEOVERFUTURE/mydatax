package com.iapppay.datax.redis;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @className: RedisUtils
 * @classDescription:redis工具类
 * @author lishiqiang
 * @create_date: 2019年5月8日 上午11:22:37
 * @update_date:
 */
@Component
public class RedisUtils {
	private Logger log = LoggerFactory.getLogger(RedisUtils.class);

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private static final ObjectMapper mapper = new ObjectMapper();

	/**
	 * 读取缓存
	 * 
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		Object result = null;
		ValueOperations<String, Object> operations = redisTemplate.opsForValue();
		result = operations.get(key);
		return result;
	}

	/**
	 * 读取系统配置缓存
	 * 
	 * @param key
	 * @return
	 */
	public Object getSystemConf(String key) {
		Object result = null;
		ValueOperations<String, Object> operations = redisTemplate.opsForValue();
		result = operations.get(RedisApplicationConstants.getSystemConfigMapKey());
		if(result!=null) {
			try {
				result = mapper.readValue(result.toString(), Map.class).get(key);
			} catch (JsonParseException e) {
				log.error("Error getting redis cache value by key[{}]--->{}", key, e.getMessage(), e);
			} catch (JsonMappingException e) {
				log.error("Error getting cache value by key[{}]--->{}", key, e.getMessage(), e);
			} catch (IOException e) {
				log.error("Error getting cache value by key[{}]---{}", key, e.getMessage(), e);
			}
		}
		return result;
	}

	/**
	 * 读取系统配置缓存
	 * 
	 * @param key
	 * @param defaultValue 默认值
	 * @return
	 */
	public Object getSystemConf(String key, Object defaultValue) {
		Object result = null;
		ValueOperations<String, Object> operations = redisTemplate.opsForValue();
		result = operations.get(RedisApplicationConstants.getSystemConfigMapKey());
		if (result != null) {
			try {
				result = mapper.readValue(result.toString(), Map.class).get(key);
			} catch (JsonParseException e) {
				log.error("Error getting redis cache value by key[{}]--->{}", key, e.getMessage(), e);
			} catch (JsonMappingException e) {
				log.error("Error getting cache value by key[{}]--->{}", key, e.getMessage(), e);
			} catch (IOException e) {
				log.error("Error getting cache value by key[{}]---{}", key, e.getMessage(), e);
			}
		}
		return result == null ? defaultValue : result;
	}

	/**
	 * 设置缓存
	 * 
	 * @param key
	 * @param value
	 */
	public void set(String key, Object value) {
		ValueOperations<String, Object> operations = redisTemplate.opsForValue();
		operations.set(key, value);
	}
}
