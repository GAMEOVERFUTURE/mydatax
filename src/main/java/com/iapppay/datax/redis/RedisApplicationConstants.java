package com.iapppay.datax.redis;

/**
 * 
 * @className: RedisApplicationConstants
 * @classDescription:redis定义的常量
 * @author lishiqiang
 * @create_date: 2019年5月8日 下午3:16:07
 * @update_date:
 */
public class RedisApplicationConstants {

	/**
	 * vd_system_config系统配置信息
	 */
	public static final String SYSTEM_CONFIG_KEY = "SYSTEM_CONFIG_MAP_KEY";

	/**
	 * 是否开启消息监听
	 */
	public static final String SP_CONSUMER_ISOPEN_KEY = "sp.consumer.isopen";

	public static final String SP_CONSUMER_ISOPEN_DEAULTVALUE = "Y";

	public static final String prefix = "";

	public static String getSystemConfigMapKey() {
		return prefix + SYSTEM_CONFIG_KEY;
	}
}
