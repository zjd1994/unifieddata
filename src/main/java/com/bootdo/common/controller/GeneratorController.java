package com.bootdo.common.controller;


import com.bootdo.common.service.GeneratorService;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
public class GeneratorController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//String prefix = "common/generator";
	@Autowired
	GeneratorService generatorService;

//	@GetMapping()
//	String generator() {
//		return prefix + "/list";
//	}

//	@ResponseBody
//	@GetMapping("/list")
//	List<Map<String, Object>> list() {
//		List<Map<String, Object>> list = generatorService.list();
//		return list;
//	};
	
	@ResponseBody
	@GetMapping(value = "/common/generator/code/{table}")
	public void code(HttpServletRequest request, HttpServletResponse response,@PathVariable("table") String table) throws Exception {
		try {
			logger.info("table:" + table);
			String[] tableNames = new String[] { table };
			byte[] data = 
					generatorService.generatorCode(tableNames);
					response.reset();
					response.setHeader("Content-Disposition", "attachment; filename=\"bootdo.zip\"");
					response.addHeader("Content-Length", "" + data.length);
					response.setContentType("application/octet-stream; charset=UTF-8");
			logger.info("++++++++++++++++++++end generate code++++++++++++++++++++");
			
					IOUtils.write(data, response.getOutputStream());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.info("e:"+e.getCause());
			logger.info("e:"+e.getMessage());
		}
	}

//	@RequestMapping("/batchCode")
//	public void batchCode(HttpServletRequest request, HttpServletResponse response, String tables) throws IOException {
//		String[] tableNames = new String[] {};
//		tableNames = JSON.parseArray(tables).toArray(tableNames);
//		byte[] data = generatorService.generatorCode(tableNames);
//		response.reset();
//		response.setHeader("Content-Disposition", "attachment; filename=\"bootdo.zip\"");
//		response.addHeader("Content-Length", "" + data.length);
//		response.setContentType("application/octet-stream; charset=UTF-8");
//
//		IOUtils.write(data, response.getOutputStream());
//	}
//
//	@GetMapping("/edit")
//	public String edit(Model model) throws Exception {
//		Configuration conf = GenUtils.getConfig();
//		Map<String, Object> property = new HashMap<>(16);
//		property.put("author", conf.getProperty("author"));
//		property.put("email", conf.getProperty("email"));
//		property.put("package", conf.getProperty("package"));
//		property.put("autoRemovePre", conf.getProperty("autoRemovePre"));
//		property.put("tablePrefix", conf.getProperty("tablePrefix"));
//		model.addAttribute("property", property);
//		return prefix + "/edit";
//	}
//
//	@ResponseBody
//	@PostMapping("/update")
//	R update(@RequestParam Map<String, Object> map) {
//		try {
//			PropertiesConfiguration conf = new PropertiesConfiguration("generator.properties");
//			conf.setProperty("author", map.get("author"));
//			conf.setProperty("email", map.get("email"));
//			conf.setProperty("package", map.get("package"));
//			conf.setProperty("autoRemovePre", map.get("autoRemovePre"));
//			conf.setProperty("tablePrefix", map.get("tablePrefix"));
//			conf.save();
//		} catch (ConfigurationException e) {
//			return R.error("保存配置文件出错");
//		}
//		return R.ok();
//	}
}
