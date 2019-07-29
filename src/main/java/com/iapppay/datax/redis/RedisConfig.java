package com.iapppay.datax.redis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

	@Bean
	@ConditionalOnMissingBean(name = "redisTemplate")
	public RedisTemplate<String, Object> setRedisTemplate(JedisConnectionFactory jedisConnectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		jedisConnectionFactory.setDatabase(1);
		redisTemplate.setConnectionFactory(jedisConnectionFactory);
		// 设置value的序列化规则和 key的序列化规则
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.afterPropertiesSet();
		System.out.println(redisTemplate);
		return redisTemplate;
	}

}
