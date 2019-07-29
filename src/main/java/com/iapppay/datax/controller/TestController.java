package com.iapppay.datax.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

	@RequestMapping("/mian")
	@ResponseBody
	public String request() {
		return "hello world";
	}
}
