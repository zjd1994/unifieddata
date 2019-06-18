package com.bootdo.system.controller;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
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

import com.bootdo.common.config.Constant;
import com.bootdo.common.controller.BaseController;
import com.bootdo.common.utils.R;
import com.bootdo.system.domain.RoleDO;
import com.bootdo.system.service.RoleService;

@RequestMapping("/sys/role")
@RestController
public class RoleController extends BaseController {
	@Autowired
	RoleService roleService;


//	@RequiresPermissions("sys:role:role")
	@GetMapping("/list")
	R list() {
		List<RoleDO> roles = roleService.list();
		return new R().put("roles", roles);
	}


	@RequiresPermissions("sys:role:edit")
	@GetMapping("/edit/{id}")
	R edit(@PathVariable("id") Long id, Model model) {
		RoleDO roleDO = roleService.get(id);
		return new R().put("roles", roleDO);
	}

	@RequiresPermissions("sys:role:add")
	@PostMapping("/save")
	R save(RoleDO role) {
		if (roleService.save(role) > 0) {
			return R.ok();
		} else {
			return R.error(Constant.STATUS_UNCHECKPASS, "保存失败");
		}
	}

	@RequiresPermissions("sys:role:edit")
	@PostMapping("/update")
	R update(RoleDO role) {
		if (roleService.update(role) > 0) {
			return R.ok();
		} else {
			return R.error(Constant.STATUS_UNCHECKPASS, "保存失败");
		}
	}

	@RequiresPermissions("sys:role:remove")
	@GetMapping("/remove/{id}")
	R remove(@PathVariable("id") Long id) {
		if (roleService.remove(id) > 0) {
			return R.ok();
		} else {
			return R.error(Constant.STATUS_UNCHECKPASS, "删除失败");
		}
	}
	
	@RequiresPermissions("sys:role:batchRemove")
	@PostMapping("/batchRemove")
	@ResponseBody
	R batchRemove(@RequestParam("ids[]") Long[] ids) {
		int r = roleService.batchremove(ids);
		if (r > 0) {
			return R.ok();
		}
		return R.error();
	}
}
