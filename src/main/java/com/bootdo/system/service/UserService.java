package com.bootdo.system.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bootdo.common.domain.Tree;
import com.bootdo.system.domain.DeptDO;
import com.bootdo.system.domain.UserDO;
import com.bootdo.system.vo.UserVO;

@Service
public interface UserService {
	UserDO get(Long id);

	List<UserDO> list(Map<String, Object> map);

	int count(Map<String, Object> map);

	int save(UserDO user);

	int update(UserDO user);

	int remove(Long userId);

	int batchremove(Long[] userIds);

	boolean exit(Map<String, Object> params);

	Set<String> listRoles(Long userId);

	int resetPwd(UserVO userVO,UserDO userDO) throws Exception;
	int adminResetPwd(UserVO userVO) throws Exception;
	Tree<DeptDO> getTree();

	/**
	 * 更新个人信息
	 * @param userDO
	 * @return
	 */
	int updatePersonal(UserDO userDO);

	/**
	 * 更新个人图片
	 * @param file 图片
	 * @param avatar_data 裁剪信息
	 * @param userId 用户ID
	 * @throws Exception
	 */
    Map<String, Object> updatePersonalImg(MultipartFile file, String avatar_data, Long userId) throws Exception;
    /**
	 * 根据数据库字典，username/name三者进行模糊查询获得用户集合。符合其一就查询出来
	 */
    List<UserDO> getUser(String supplier,String name,String tester);
    /**
	 * 获得相应项目的对应用户信息
	 * @param projectId
	 * @return
	 */
    List<UserDO> getProjectUser(String projectId);
    /**
	 * 删除旧的工程-用户表信息，添加新提交的信息
	 */
   int  saveUserForProject(List<Integer> userIds,String projectId);
   /**
	 * 用户名重复验证
	 */
	UserDO isRepeat(String userName);
}
