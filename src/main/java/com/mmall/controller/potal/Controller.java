package com.mmall.controller.potal;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@org.springframework.stereotype.Controller
public class Controller{
	@RequestMapping(value = "/moocl/test.do" ,method = RequestMethod.GET)
	public void test() {
		System.out.println("这里是controller");
	}
	@RequestMapping(value = "/*")
	public Object test1() {
		System.out.println("这里是controller");
		return "forward:/test.jsp";
	}
}
