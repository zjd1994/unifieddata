package com.bootdo.common.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Select;

public interface GeneratorMapper {
	@Select("select * from user_tab_comments")
	List<Map<String, Object>> list();

	@Select("select count(*) from user_tab_comments")
	int count(Map<String, Object> map);

	@Select("select * from user_tab_comments where table_name = #{tableName}")
	Map<String, String> get(String tableName);

	@Select("select * from user_tab_columns where table_name = #{tableName}")
	List<Map<String, String>> listColumns(String tableName);
}
