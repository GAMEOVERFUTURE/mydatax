package com.iapppay.datax.redis;

/**
 * 
 * @className: RedisDbEnum
 * @classDescription:redis存储数据库
 * @author lishiqiang
 * @create_date: 2019年5月8日 下午2:08:01
 * @update_date:
 */
public enum RedisDbEnum {

	DB0(0),

	DB1(1),

	DB2(2);

	public Integer value;

	RedisDbEnum(Integer value) {
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
}
