package com.bootdo.common.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bootdo.common.utils.MD5Utils;
import com.bootdo.system.domain.UserDO;
import com.bootdo.system.service.UserService;
@RestController
public class DataController {
	@Autowired
	UserService UserService;
	@RequestMapping(value="/data",produces = "application/json")
	public String getData(@RequestParam String username,@RequestParam String password) {
		//System.err.println(username+","+password);
		password = MD5Utils.encrypt(username, password);
		Map<String,Object>map=new HashMap<>();
		map.put("username", username);
		map.put("password", password);
		List<UserDO>list=UserService.list(map);
		if(list==null||list.size()==0) {
			return "账号或密码不正确";
		}
		// 查询用户信息
		UserDO user = list.get(0);
		
		// 账号不存在
		if (user == null) {
			return "账号或密码不正确";
		}
		// 密码错误
		if (!password.equals(user.getPassword())) {
			return "账号或密码不正确";
		}
		// 账号锁定
		if (user.getStatus() == 0) {
			return "账号已被锁定,请联系管理员";
		}
		return "{key:1,value:2}";
	}
}
