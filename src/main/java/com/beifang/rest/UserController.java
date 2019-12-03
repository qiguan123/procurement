package com.beifang.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login() {
		return "ok";
	}
	
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	public String info() {
		return "ok";
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public String loginOut() {
		return "ok";
	}
	
}
