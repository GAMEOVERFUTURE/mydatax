package com.iapppay.datax;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 
 * @className: DataxAdminApplication
 * @classDescription:
 * @author lishiqiang
 * @create_date: 2019年7月18日 上午9:55:44
 * @update_date:
 */
@EnableScheduling
@SpringBootApplication
@MapperScan(basePackages = "com.iapppay.operating.model.sp")
public class DataxAdminApplication {
	public static void main(String[] args) {
		String dataxPath = System.getProperty("user.dir");
		System.setProperty("basedir", dataxPath);
		SpringApplication.run(DataxAdminApplication.class, args);
	}

}
