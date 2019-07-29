package com.iapppay.datax.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @className: ClassLoaderUtil
 * @classDescription:
 * @author lishiqiang
 * @create_date: 2019年3月20日 下午3:53:58
 * @update_date:
 */
public class ClassLoaderUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClassLoaderUtil.class);

	public static ClassLoader getClassLoader() {
		ClassLoader classLoader = null;
		try {
			classLoader = Thread.currentThread().getContextClassLoader();

			if (classLoader == null) {
				classLoader = ClassLoaderUtil.class.getClassLoader();
			}
		} catch (SecurityException se) {
			LOGGER.error("get classLoader fail");
		}
		return classLoader;
	}
}
