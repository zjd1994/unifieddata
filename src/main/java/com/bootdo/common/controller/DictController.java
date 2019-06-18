package com.bootdo.common.controller;


import com.bootdo.common.domain.DictDO;
import com.bootdo.common.domain.Tree;
import com.bootdo.common.service.DictService;
import com.bootdo.common.utils.PageUtils;
import com.bootdo.common.utils.Query;
import com.bootdo.common.utils.R;
import com.bootdo.system.domain.DeptDO;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

/**
 * 字典表
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2017-09-29 18:28:07
 */

@RestController
@RequestMapping("/common/dict")
public class DictController extends BaseController {
	@Autowired
	private DictService dictService;

	
	@PostMapping("/list")
	@RequiresPermissions("common:dict:dict")
	public R list(@RequestParam Map<String, Object> params) {
		// 查询列表数据
		Query query = new Query(params);
		List<DictDO> dictList = dictService.list(query);
		int total = dictService.count(query);
		PageUtils pageUtils = new PageUtils(dictList, total);
		return new R().put("data", pageUtils);
	}

	@GetMapping("/edit/{id}")
	@RequiresPermissions("common:dict:edit")
	public R edit(@PathVariable("id") Long id, Model model) {
		R result = new R();
		DictDO dict = dictService.get(id);
		result.put("dict", dict);
		return result;
	}

	/**
	 * 保存
	 */
	@PostMapping("/save")
	@RequiresPermissions("common:dict:add")
	public R save(DictDO dict) {
		if (dictService.save(dict) > 0) {
			return R.ok();
		}
		return R.error();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	@RequiresPermissions("common:dict:edit")
	public R update(DictDO dict) {
		dictService.update(dict);
		return R.ok();
	}

	/**
	 * 删除
	 */
	@GetMapping("/remove/{id}")
	@RequiresPermissions("common:dict:remove")
	public R remove(@PathVariable("id") Long id) {
		if (dictService.remove(id) > 0) {
			return R.ok();
		}
		return R.error();
	}

	/**
	 * 删除
	 */
	@PostMapping("/batchRemove")
	@RequiresPermissions("common:dict:batchRemove")
	public R remove(@RequestParam("ids[]") Long[] ids) {
		dictService.batchRemove(ids);
		return R.ok();
	}

	@GetMapping("/type")
	public R listType() {
		return new R().put("typelist", dictService.listType());
	};

	// 类别已经指定增加
	@GetMapping("/add/{type}/{description}")
	@RequiresPermissions("common:dict:add")
	R addD(Model model, @PathVariable("type") String type, @PathVariable("description") String description) {
		model.addAttribute("type", type);
		model.addAttribute("description", description);
		return new R().put("model", model);
	}
	 
	/*获得数据库字典的类型树的数据结构*/
	@GetMapping("/tree")
	@ResponseBody
	public R tree() {
		Tree<DeptDO> tree = new Tree<DeptDO>();
		tree = dictService.getTree();
		return new R().put("tree", tree);
	}
}
