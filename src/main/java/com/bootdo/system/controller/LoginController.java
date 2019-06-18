package com.bootdo.system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bootdo.common.config.ApplicationContextRegister;
import com.bootdo.common.controller.BaseController;
import com.bootdo.common.domain.FileDO;
import com.bootdo.common.domain.Tree;
import com.bootdo.common.service.FileService;
import com.bootdo.common.utils.MD5Utils;
import com.bootdo.common.utils.R;
import com.bootdo.common.utils.ShiroUtils;
import com.bootdo.system.domain.MenuDO;
import com.bootdo.system.service.MenuService;

@RestController
public class LoginController extends BaseController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	MenuService menuService;
	@Autowired
	FileService fileService;

	@GetMapping({ "/index" })
	R index(Model model) {
		List<Tree<MenuDO>> menus = menuService.listMenuTree(getUserId());
		R result = new R();
		result.put("menus", menus);
		result.put("name", getUser().getName());
		FileDO fileDO = fileService.get(getUser().getPicId()+"");
		if(fileDO!=null&&fileDO.getUrl()!=null){
			if(fileService.isExist(fileDO.getUrl())){
				result.put("picUrl", fileDO.getUrl());
			}else {
				
			}
		}else {
			result.put("picUrl", "/img/photo_s.jpg");
		}
		result.put("username", getUser().getUsername());
		return result;
	}
	@PostMapping("/login")
	R ajaxLogin(@RequestBody Map<String, Object> input) {
		String username=(String) input.get("username");
		String password=(String) input.get("password");
		System.err.println(username+","+password);
		password = MD5Utils.encrypt(username, password);
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		Subject subject = SecurityUtils.getSubject();
		try {
			System.err.println("yyyyyyyyyy");
			subject.login(token);
			System.err.println("xxxxxxxxxx");
            R result = new R();
            result.put("token", subject.getSession().getId());
            result.put("user",ShiroUtils.getUser());
            MenuService menuService = ApplicationContextRegister.getBean(MenuService.class);
    		Set<String> perms = menuService.listPerms(ShiroUtils.getUser().getUserId());
    		result.put("perms",perms);
			return result;
		} catch (AuthenticationException e) {
			return R.error("用户或密码错误");
		}catch (Exception e) {
			// TODO: handle exception
			return R.error("重新登录");
		}
	}

	@GetMapping("/logout")
	R logout() {
		ShiroUtils.logout();
		return R.ok();
	}
	/**
	 * 未登录，shiro应重定向到登录界面，此处返回未登录状态信息由前端控制跳转页面
     * @return
     */
    @RequestMapping(value = "/unauth")
    @ResponseBody
    public Object unauth() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", "1000000");
        map.put("msg", "未登录");
        return map;
    }

}
