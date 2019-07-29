package com.iapppay.datax.service;

import org.springframework.stereotype.Component;

@Component
public class Testa extends CpcashConfiguration {

	public void sss() {
		System.out.println(getMongoDbAddress());
	}
}
