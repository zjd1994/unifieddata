package com.bootdo.system.controller;

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
import com.bootdo.common.domain.Query;
import com.bootdo.common.domain.Tree;
import com.bootdo.common.utils.PageUtils;
import com.bootdo.common.utils.R;
import com.bootdo.system.domain.MenuDO;
import com.bootdo.system.service.MenuService;

/**
 * @author bootdo 1992lcg@163.com
 */
@RequestMapping("/sys/menu")
@RestController
public class MenuController extends BaseController {
	@Autowired
	MenuService menuService;


	@RequiresPermissions("sys:menu:menu")
	@RequestMapping("/list")
	R list(@RequestParam Map<String, Object> params) {
		Query query = new Query(params);
		List<MenuDO> menus = menuService.list(query);
		int total = menuService.count(query);
		PageUtils pageUtils = new PageUtils(menus, total);
		return new R().put("data", pageUtils);
	}

	@RequiresPermissions("sys:menu:add")
	@GetMapping("/getPmenu/{pId}")
	R getPmenu(Model model, @PathVariable("pId") Long pId) {
		R result = new R();
		result.put("pId", pId);
		if (pId == 0) {
			result.put("pName", "根目录");
		} else {
			result.put("pName", menuService.get(pId).getName());
		}
		return result;
	}

	@RequiresPermissions("sys:menu:edit")
	@GetMapping("/edit/{id}")
	R edit(Model model, @PathVariable("id") Long id) {
		R result = new R();
		MenuDO mdo = menuService.get(id);
		Long pId = mdo.getParentId();
		result.put("pId", pId);
		if (pId == 0) {
			result.put("pName", "根目录");
		} else {
			result.put("pName", menuService.get(pId).getName());
		}
		result.put("menu", mdo);
		return result;
	}

	@RequiresPermissions("sys:menu:add")
	@PostMapping("/save")
	R save(MenuDO menu) {
		System.out.println(menu.getName());
		System.out.println(menu.getParentId());
		if (menuService.save(menu) > 0) {
			return R.ok();
		} else {
			return R.error(Constant.STATUS_SERVERERROR, "保存失败");
		}
	}

	@RequiresPermissions("sys:menu:edit")
	@PostMapping("/update")
	R update(MenuDO menu) {
		if (menuService.update(menu) > 0) {
			return R.ok();
		} else {
			return R.error(Constant.STATUS_SERVERERROR, "更新失败");
		}
	}

	@RequiresPermissions("sys:menu:remove")
	@GetMapping("/remove/{id}")
	R remove(@PathVariable("id") Long id) {
		if (menuService.remove(id) > 0) {
			return R.ok();
		} else {
			return R.error(Constant.STATUS_SERVERERROR, "删除失败");
		}
	}

	@GetMapping("/tree")
	R tree() {
		Tree<MenuDO>  tree = menuService.getTree();
		System.out.println(tree.toString());
		return new R().put("tree", tree);
	}

	@GetMapping("/tree/{roleId}")
	R tree(@PathVariable("roleId") Long roleId) {
		Map<String, Object> map = menuService.getTree(roleId);
		R result = new R();
		result.put("tree", map.get("tree"));
		result.put("selectedmenuids", map.get("selectedtreeids"));
		return result;
	}
}
