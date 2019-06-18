package com.bootdo.system.dao;

import com.bootdo.system.domain.UserDO;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2017-10-03 09:45:11
 */
@Mapper
public interface UserDao {

	UserDO get(Long userId);
	
	List<UserDO> list(Map<String,Object> map);
	
	int count(Map<String,Object> map);
	
	int save(UserDO user);
	
	int update(UserDO user);
	
	int remove(Long userId);
	
	int batchRemove(Long[] userIds);
	
	Long[] listAllDept();
	
	//根据数据库字典，username/name三者进行模糊查询获得用户集合
	List<UserDO> getUser(String supplier,String name,String tester);
	/**
	 * 获得相应项目的对应用户信息
	 * @param projectId
	 * @return
	 */
	List<UserDO> getProjectUser(String projectId);
	/**
	 * 用户名重复验证
	 */
	UserDO isRepeat(String userName);
}
