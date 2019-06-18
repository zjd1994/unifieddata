package com.bootdo.system.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.bootdo.common.config.Constant;
import com.bootdo.common.controller.BaseController;
import com.bootdo.common.domain.Query;
import com.bootdo.common.domain.Tree;
import com.bootdo.common.service.DictService;
import com.bootdo.common.utils.MD5Utils;
import com.bootdo.common.utils.PageUtils;
import com.bootdo.common.utils.R;
import com.bootdo.system.domain.DeptDO;
import com.bootdo.system.domain.RoleDO;
import com.bootdo.system.domain.UserDO;
import com.bootdo.system.service.RoleService;
import com.bootdo.system.service.UserService;
import com.bootdo.system.vo.UserVO;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@RequestMapping("/sys/user")
@RestController
public class UserController extends BaseController {
	Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired
	UserService userService;
	@Autowired
	RoleService roleService;
	@Autowired
	DictService dictService;

	@RequestMapping("/list")
	R list(@RequestParam Map<String, Object> params) {
		// 查询列表数据
		Query query = new Query(params);
		List<UserDO> sysUserList = userService.list(query);
		int total = userService.count(query);
		PageUtils pageUtil = new PageUtils(sysUserList, total);
		return new R().put("data", pageUtil);
	}

	@RequiresPermissions("sys:user:add")
	@GetMapping("/add")
	R getRoles(Model model) {
		List<RoleDO> roles = roleService.list();
		return new R().put("roles", roles);
	}

	@RequiresPermissions("sys:user:edit")
	@GetMapping("/edit/{id}")
	R edit(Model model, @PathVariable("id") Long id) {
		R result = new R();
		UserDO userDO = userService.get(id);
		result.put("user", userDO);
//		List<RoleDO> roles = roleService.list(id);
//		if(roles.size()>0) {
//			result.put("roles", roles.get(0).getRoleId());
//		}
		return result;
	}

	@RequiresPermissions("sys:user:add")
	@PostMapping("/save")
	R save(@RequestBody UserDO user) {
		user.setPassword(MD5Utils.encrypt(user.getUsername(), user.getPassword()));
		Long deptid = (long) 13;
		user.setDeptId(deptid);
		int result = userService.save(user);
		if(result==-1) {
			return R.error("用户名重复！");
		}
		if (result> 0) {
			return R.ok();
		}
		return R.error("未知原因，保存失败！");
	}

	@RequiresPermissions("sys:user:edit")
	@PostMapping("/update")
	R update(@RequestBody UserDO user) {
		int result = userService.update(user);
		if(result==-1) {
			return R.error("用户名重复！");
		}
		if (result> 0) {
			return R.ok();
		}
		return R.error("未知原因，修改失败！");
	}


	@RequiresPermissions("sys:user:edit")
	@PostMapping("/updatePeronal")
	R updatePeronal(@RequestBody UserDO user) {
		int result = userService.updatePersonal(user);
		if(result==-1) {
			return R.error("用户名重复！");
		}
		if ( result> 0) {
			return R.ok();
		}
		return R.error("未知原因，修改失败！");
	}


	@RequiresPermissions("sys:user:remove")
	@GetMapping("/remove/{id}")
	R remove(@PathVariable("id") Long id) {
		if (userService.remove(id) > 0) {
			return R.ok();
		}
		return R.error();
	}

	//@RequiresPermissions("sys:user:batchRemove")
	@PostMapping("/batchRemove")
	R batchRemove(@RequestParam("ids[]") Long[] userIds) {
		int r = userService.batchremove(userIds);
		if (r > 0) {
			return R.ok();
		}
		return R.error();
	}

	@PostMapping("/exit")
	@ResponseBody
	R exit(@RequestParam Map<String, Object> params) {
		// 存在，不通过，false
		if(userService.exit(params)) {
			return R.ok("true");
		} else {
			return R.ok("false");
		}
	}

	@RequiresPermissions("sys:user:resetPwd")
	@GetMapping("/resetPwd/{id}")
	R resetPwd(@PathVariable("id") Long userId, Model model) {

		UserDO userDO = new UserDO();
		userDO.setUserId(userId);
		return new R().put("user", userDO);
	}

	@PostMapping("/resetPwd")
	@ResponseBody
	R resetPwd(UserVO userVO) {
		try{
			userService.resetPwd(userVO,getUser());
			return R.ok();
		}catch (Exception e){
			return R.error(Constant.STATUS_SERVERERROR,e.getMessage());
		}

	}
	@RequiresPermissions("sys:user:resetPwd")
	@PostMapping("/adminResetPwd")
	R adminResetPwd(UserVO userVO) {
		try{
			userService.adminResetPwd(userVO);
			return R.ok();
		}catch (Exception e){
			return R.error(Constant.STATUS_SERVERERROR,e.getMessage());
		}

	}
	@GetMapping("/tree")
	R  tree() {
		Tree<DeptDO> tree = new Tree<DeptDO>();
		tree = userService.getTree();
		return new R().put("tree", tree);
	}


	@GetMapping("/personal")
	R personal(Model model) {
		R result = new R();
		UserDO userDO  = userService.get(getUserId());
		result.put("user",userDO);
		result.put("hobbyList",dictService.getHobbyList(userDO));
		result.put("sexList",dictService.getSexList());
		return result;
	}
	@PostMapping("/uploadImg")
	R uploadImg(@RequestParam("avatar_file") MultipartFile file, String avatar_data, HttpServletRequest request) {
		if ("test".equals(getUsername())) {
			return R.error(Constant.STATUS_SERVERERROR, "演示系统不允许修改,完整体验请部署程序");
		}
		Map<String, Object> result = new HashMap<>();
		try {
			result = userService.updatePersonalImg(file, avatar_data, getUserId());
		} catch (Exception e) {
			return R.error("更新图像失败！");
		}
		if(result!=null && result.size()>0){
			return R.ok(result);
		}else {
			return R.error("更新图像失败！");
		}
	}

	@PostMapping("/getEmpData")
	public List<Map<String,Object>> getEmpData(){
		UserDO userDO = (UserDO) SecurityUtils.getSubject().getPrincipal();

		logger.info("deptId:"+userDO.getDeptId());
		Map<String,Object> map =new HashMap<>();
		map.put("deptId",userDO.getDeptId());
		List<UserDO> list = userService.list(map);
		logger.info("list:"+list);
		List<Map<String,Object>> dataList = new ArrayList<>();
		if (list != null && list.size() != 0){
			for (int i = 0 ; i < list.size() ; i++){
				Map<String,Object> temp = new HashMap<>();
				UserDO user = list.get(i);
				temp.put("username",user.getUsername());
				temp.put("name",user.getName());
				temp.put("id",user.getUserId());
				dataList.add(temp);
			}
		}
		return  dataList;
	}
	
	@PostMapping("/getUser")
	public R getUser(String supplier, String name,String tester) {
		if(supplier!=null&&supplier !="") {
			supplier="%"+supplier+"%";
		}
		if(name!=null) {
			name="%"+name+"%";
		}else {
			name="%%";
		}
		List<UserDO> userList=userService.getUser(supplier, name,tester);
		return new R().put("userList", userList);
	}
	/**
	 * 获得相应工程的所有用户信息
	 * @param projectId
	 * @return
	 */
	@GetMapping("/getProjectUser/{projectId}")
	public R getProjectUser(@PathVariable("projectId")String projectId) {
		List<UserDO> userProjectList=userService.getProjectUser(projectId);
		return new R().put("userProjectList", userProjectList);
	}
	/**
	 * 删除旧的工程-用户表信息，添加新提交的信息
	 */
	@PostMapping("/saveUserForProject")
	public R saveUserForProject(@RequestBody Map<String, Object> canshu) {
		List<Integer> userIds=null;
		try {
			userIds=(List<Integer>)canshu.get("userIds");
		} catch (Exception e) {
			// TODO: handle exception
			return R.error("用户id格式不正确");
		}
		int result =userService.saveUserForProject(userIds, (String)canshu.get("projectId"));
		if(result==1) {
			return R.ok();
		}else {
			return R.error();
		}
	}
	@PostMapping("/isRepeat")
	R isRepeat(String userName,String userId,String page) {
		System.out.println(userName);
		System.out.println(page);
		UserDO userDO=userService.isRepeat(userName);
		if(userDO==null) {//没有重名
			return R.ok("0");//0来代表没有重名
		}else if("update".equals(page)) {
			if(!(userId.equals(userDO.getUserId().toString()))) {
				return R.ok("1");//1来代表重名
	    	}else {
	    		return R.ok("0");//0来代表没有重名
	    	}
		}
		return R.ok("1");//1来代表重名
	}
}
