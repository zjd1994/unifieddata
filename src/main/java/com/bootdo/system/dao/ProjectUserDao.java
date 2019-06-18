package com.bootdo.system.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.bootdo.system.domain.UserDO;
@Mapper
public interface ProjectUserDao {
	int  saveUserForProject(List<Integer> userIds,String projectId);
	int removeUserForProject(String projectId);
}
