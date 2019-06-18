package com.bootdo.common.service.ipml;

import java.io.ByteArrayOutputStream;
import java.io.File;

import java.util.List;
import java.util.Map;

import java.util.zip.ZipOutputStream;



import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bootdo.common.dao.GeneratorMapper;
import com.bootdo.common.service.GeneratorService;
import com.bootdo.common.utils.GenUtils;



@Service
public class GeneratorServiceImpl implements GeneratorService {
	@Autowired
	GeneratorMapper generatorMapper;

	@Override
	public List<Map<String, Object>> list() {
		List<Map<String, Object>> list = generatorMapper.list();
		return list;
	}

	@Override
	public byte[] generatorCode(String[] tableNames) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ZipOutputStream zip = new ZipOutputStream(outputStream);
		for(String tableName : tableNames){
			//查询表信息
			Map<String, String> table = generatorMapper.get(tableName);
			//查询列信息
			List<Map<String, String>> columns = generatorMapper.listColumns(tableName);
			//生成代码
			GenUtils.generatorCode(table, columns, zip);
		}
		IOUtils.closeQuietly(zip);
		return outputStream.toByteArray();
	}





	/**
	 * 获取文件名
	 */
	@SuppressWarnings("null")
	public static String getFileName(String template, String className, String packageName, String moduleName) {
		String packagePath = "main" + File.separator + "java" + File.separator;
		String frontPath = "ui" + File.separator;
		if (packageName != null || !packageName.equals("")) {
			packagePath += packageName.replace(".", File.separator) + File.separator;
		}

		if (template.contains("index.vue.vm")) {
			return frontPath + "views" + File.separator + moduleName + File.separator + toLowerCaseFirstOne(className) + File.separator + "index.vue";
		}

		if (template.contains("service.java.vm")) {
			return packagePath + "service" + File.separator + className + "Service.java";
		}
		if (template.contains("mapper.java.vm")) {
			return packagePath + "dao" + File.separator + className + "Dao.java";
		}
		if (template.contains("entity.java.vm")) {
			return packagePath + "entity" + File.separator + className + ".java";
		}
		if (template.contains("controller.java.vm")) {
			return packagePath + "controller" + File.separator + className + "Controller.java";
		}
		if (template.contains("serviceImpl.java.vm")) {
			return packagePath + "service" + File.separator +"impl"+File.separator+ className + "ServiceImpl.java";
		}
		if (template.contains("mapper.xml.vm")) {
			return "main" + File.separator + "resources" + File.separator + "mapper" + File.separator + className + "Mapper.xml";
		}

		return null;
	}

	//首字母转小写
	public static String toLowerCaseFirstOne(String s) {
		if (Character.isLowerCase(s.charAt(0))) {
			return s;
		} else {
			return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
		}
	}

	/**
	 * 获取配置信息
	 * @throws org.apache.commons.configuration.ConfigurationException 
	 */
	public static Configuration getConfig() throws org.apache.commons.configuration.ConfigurationException {
		return new PropertiesConfiguration("generator.properties");
	}

}
