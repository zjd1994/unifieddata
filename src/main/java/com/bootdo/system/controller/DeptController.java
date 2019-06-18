package com.bootdo.system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bootdo.common.config.Constant;
import com.bootdo.common.controller.BaseController;
import com.bootdo.common.domain.Tree;
import com.bootdo.common.utils.R;
import com.bootdo.system.domain.DeptDO;
import com.bootdo.system.service.DeptService;

/**
 * 部门管理
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2017-09-27 14:40:36
 */

@RestController
@RequestMapping("/system/sysDept")
public class DeptController extends BaseController {
	@Autowired
	private DeptService sysDeptService;

	@GetMapping("/list")
	@RequiresPermissions("system:sysDept:sysDept")
	public R list() {
		Map<String, Object> query = new HashMap<>(16);
		R result = new R();
		List<DeptDO> sysDeptList = sysDeptService.list(query);
		result.put("code", "200");
		result.put("data", sysDeptList);
		return result;
	}

	@GetMapping("/getparent/{pId}")
	@RequiresPermissions("system:sysDept:getparent")
	public R getParentdept(@PathVariable("pId") Long pId, Model model) {
		model.addAttribute("pId", pId);
		Map<String, Object> resultmap = new HashMap<>();
		resultmap.put("pId", pId);
		if (pId == 0) {
			resultmap.put("pName", "测试部");
		} else {
			resultmap.put("pName", sysDeptService.get(pId).getName());
		}
		resultmap.put("code", "200");
		return R.ok(resultmap);
	}

	@GetMapping("/edit/{deptId}")
	@RequiresPermissions("system:sysDept:edit")
	public R edit(@PathVariable("deptId") Long deptId, Model model) {
		Map<String, Object> resultmap = new HashMap<>();
		DeptDO sysDept = sysDeptService.get(deptId);
		resultmap.put("sysDept", sysDept);
		if(Constant.DEPT_ROOT_ID.equals(sysDept.getParentId())) {
			resultmap.put("parentDeptName", "无");
		}else {
			DeptDO parDept = sysDeptService.get(sysDept.getParentId());
			resultmap.put("parentDeptName", parDept.getName());
		}
		resultmap.put("code", Constant.STATUS_SUCESS);
		return R.ok(resultmap);
	}

	/**
	 * 保存
	 */
	@ResponseBody
	@PostMapping("/save")
	@RequiresPermissions("system:sysDept:add")
	public R save(DeptDO sysDept) {
		if (sysDeptService.save(sysDept) > 0) {
			return R.ok();
		}
		return R.error();
	}

	/**
	 * 修改
	 */
	@ResponseBody
	@RequestMapping("/update")
	@RequiresPermissions("system:sysDept:edit")
	public R update(DeptDO sysDept) {
		if (sysDeptService.update(sysDept) > 0) {
			return R.ok();
		}
		return R.error();
	}

	/**
	 * 删除
	 */
	@PostMapping("/remove")
	@ResponseBody
	@RequiresPermissions("system:sysDept:remove")
	public R remove(Long deptId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("parentId", deptId);
		if(sysDeptService.count(map)>0) {
			return R.error(Constant.STATUS_UNCHECKPASS, "包含下级部门,不允许修改");
		}
		if(sysDeptService.checkDeptHasUser(deptId)) {
			if (sysDeptService.remove(deptId) > 0) {
				return R.ok();
			}
		}else {
			return R.error(Constant.STATUS_UNCHECKPASS, "部门包含用户,不允许修改");
		}
		return R.error();
	}

	/**
	 * 删除
	 */
	@PostMapping("/batchRemove")
	@ResponseBody
	@RequiresPermissions("system:sysDept:batchRemove")
	public R remove(@RequestParam("ids[]") Long[] deptIds) {
		sysDeptService.batchRemove(deptIds);
		return R.ok();
	}

	@GetMapping("/tree")
	@ResponseBody
	public R  tree() {
		Tree<DeptDO> tree = new Tree<DeptDO>();
		tree = sysDeptService.getTree();
		return new R().put("tree",tree);
	}
}
