package com.bootdo.system.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bootdo.common.config.BootdoConfig;
import com.bootdo.common.domain.Tree;
import com.bootdo.common.utils.BuildTree;
import com.bootdo.common.utils.MD5Utils;
import com.bootdo.system.dao.DeptDao;
import com.bootdo.system.dao.ProjectUserDao;
import com.bootdo.system.dao.RoleDao;
import com.bootdo.system.dao.UserDao;
import com.bootdo.system.dao.UserRoleDao;
import com.bootdo.system.domain.DeptDO;
import com.bootdo.system.domain.RoleDO;
import com.bootdo.system.domain.UserDO;
import com.bootdo.system.domain.UserRoleDO;
import com.bootdo.system.service.UserService;
import com.bootdo.system.vo.UserVO;

//@CacheConfig(cacheNames = "user")
@Transactional
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userMapper;
    @Autowired
    RoleDao roleMapper;
    @Autowired
    UserRoleDao userRoleMapper;
    @Autowired
    DeptDao deptMapper;
    @Autowired
    ProjectUserDao projectUserMapper;
//    @Autowired
//    private FileService sysFileService;
    @Autowired
    private BootdoConfig bootdoConfig;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Override
//    @Cacheable(key = "#id")
    public UserDO get(Long id) {
        List<Long> roleIds = userRoleMapper.listRoleId(id);
        UserDO user = userMapper.get(id);
        user.setDeptName(deptMapper.get(user.getDeptId()).getName());
        if(roleIds.size()>0) {
        	user.setRoleIds(roleIds.get(0));
        }
        return user;
    }

    @Override
    public List<UserDO> list(Map<String, Object> map) {
        return userMapper.list(map);
    }

    @Override
    public int count(Map<String, Object> map) {
        return userMapper.count(map);
    }

    @Transactional
    @Override
    public int save(UserDO user) {
    	//验证userName是否重复
    	UserDO userDo=userMapper.isRepeat(user.getUsername());
    	if(userDo!=null) {
    		return -1;
    	}
        int count = userMapper.save(user);
        Long userId = user.getUserId();
        Long roleid = user.getRoleIds();
        List<UserRoleDO> list = new ArrayList<>();
        if(!"".equals(roleid)){
	        	//通过roleID查询出相应的role获得role_sign和remark的值
	        	RoleDO role1=roleMapper.get(roleid);
	        	String type="";
	        	//判断remark的值，获得相应的usertype
	        	if("拥有最高权限".equals(role1.getRemark())) {
	        		type="1";
	        	}else if("测试员".equals(role1.getRemark())) {
	        		type="4";
	        	}else if("供应商经理".equals(role1.getRemark())) {
	        		type="3";
	        	}else if("功能区管理员".equals(role1.getRemark())) {
	        		type="2";
	        	}
	        	//更新user的role_sign和usertype的值
	        	UserDO u=new UserDO();
	        	u.setUsertype(type);
	        	u.setRolesign(role1.getRoleSign());
	        	u.setUserId(userId);
	        	userMapper.update(u);
	            UserRoleDO ur = new UserRoleDO();
	            ur.setUserId(userId);
	            ur.setRoleId(roleid);
	            list.add(ur);
        }
        if (list.size() > 0) {
            userRoleMapper.batchSave(list);
        }
        return count;
    }

    @Override
    public int update(UserDO user) {
    	//验证userName是否重复
    	UserDO userDo=userMapper.isRepeat(user.getUsername());
    	if(userDo!=null&&!(user.getUserId().equals(userDo.getUserId()))) {
    		return -1;
    	}
    	Long roleid = user.getRoleIds();
        int r = userMapper.update(user);
        Long userId = user.getUserId();
        userRoleMapper.removeByUserId(userId);
        List<UserRoleDO> list = new ArrayList<>();
        if(!"".equals(roleid)){
        	//通过roleID查询出相应的role获得role_sign和remark的值
        	RoleDO role1=roleMapper.get(roleid);
        	String type="";
        	//判断remark的值，获得相应的usertype
        	if("拥有最高权限".equals(role1.getRemark())) {
        		type="1";
        	}else if("测试员".equals(role1.getRemark())) {
        		type="4";
        	}else if("供应商经理".equals(role1.getRemark())) {
        		type="3";
        	}else if("功能区管理员".equals(role1.getRemark())) {
        		type="2";
        	}
        	//更新user的role_sign和usertype的值
        	UserDO u=new UserDO();
        	u.setUsertype(type);
        	u.setRolesign(role1.getRoleSign());
        	u.setUserId(userId);
        	userMapper.update(u);
	            UserRoleDO ur = new UserRoleDO();
	            ur.setUserId(userId);
	            ur.setRoleId(roleid);
	            list.add(ur);
        }
        if (list.size() > 0) {
            userRoleMapper.batchSave(list);
        }
        return r;
    }

    @Override
    public int remove(Long userId) {
        userRoleMapper.removeByUserId(userId);
        return userMapper.remove(userId);
    }

    @Override
    public boolean exit(Map<String, Object> params) {
        boolean exit;
        exit = userMapper.list(params).size() > 0;
        return exit;
    }

    @Override
    public Set<String> listRoles(Long userId) {
        return null;
    }

    @Override
    public int resetPwd(UserVO userVO, UserDO userDO) throws Exception {
        if (Objects.equals(userVO.getUserDO().getUserId(), userDO.getUserId())) {
            if (Objects.equals(MD5Utils.encrypt(userDO.getUsername(), userVO.getPwdOld()), userDO.getPassword())) {
                userDO.setPassword(MD5Utils.encrypt(userDO.getUsername(), userVO.getPwdNew()));
                return userMapper.update(userDO);
            } else {
                throw new Exception("输入的旧密码有误！");
            }
        } else {
            throw new Exception("你修改的不是你登录的账号！");
        }
    }

    @Override
    public int adminResetPwd(UserVO userVO) throws Exception {
        UserDO userDO = get(userVO.getUserDO().getUserId());
        if ("admin".equals(userDO.getUsername())) {
            throw new Exception("超级管理员的账号不允许直接重置！");
        }
        userDO.setPassword(MD5Utils.encrypt(userDO.getUsername(), userVO.getPwdNew()));
        return userMapper.update(userDO);


    }

    @Transactional
    @Override
    public int batchremove(Long[] userIds) {
        int count = userMapper.batchRemove(userIds);
        userRoleMapper.batchRemoveByUserId(userIds);
        return count;
    }

    @Override
    public Tree<DeptDO> getTree() {
        List<Tree<DeptDO>> trees = new ArrayList<Tree<DeptDO>>();
        List<DeptDO> depts = deptMapper.list(new HashMap<String, Object>(16));
        Long[] pDepts = deptMapper.listParentDept();
        Long[] uDepts = userMapper.listAllDept();
        Long[] allDepts = (Long[]) ArrayUtils.addAll(pDepts, uDepts);
        for (DeptDO dept : depts) {
            if (!ArrayUtils.contains(allDepts, dept.getDeptId())) {
                continue;
            }
            Tree<DeptDO> tree = new Tree<DeptDO>();
            tree.setId(dept.getDeptId().toString());
            tree.setParentId(dept.getParentId().toString());
            tree.setText(dept.getName());
            Map<String, Object> state = new HashMap<>(16);
            state.put("opened", true);
            state.put("mType", "dept");
            tree.setState(state);
            trees.add(tree);
        }
        List<UserDO> users = userMapper.list(new HashMap<String, Object>(16));
        for (UserDO user : users) {
            Tree<DeptDO> tree = new Tree<DeptDO>();
            tree.setId(user.getUserId().toString());
            tree.setParentId(user.getDeptId().toString());
            tree.setText(user.getName());
            Map<String, Object> state = new HashMap<>(16);
            state.put("opened", true);
            state.put("mType", "user");
            tree.setState(state);
            trees.add(tree);
        }
        // 默认顶级菜单为０，根据数据库实际情况调整
        Tree<DeptDO> t = BuildTree.build(trees);
        return t;
    }

    @Override
    public int updatePersonal(UserDO userDO) {
    	//验证userName是否重复
    	UserDO user=userMapper.isRepeat(userDO.getUsername());
    	if(user!=null&&!(userDO.getUserId().equals(user.getUserId()))) {
    		return -1;
    	}else {
    		
    		return userMapper.update(userDO);
    	}
    }

	@Override
	public Map<String, Object> updatePersonalImg(MultipartFile file, String avatar_data, Long userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * 根据数据库字典，username/name三者进行模糊查询获得用户集合。符合其一就查询出来
	 */
	@Override
	public List<UserDO> getUser(String supplier, String name,String tester) {
		// TODO Auto-generated method stub
		return userMapper.getUser(supplier, name,tester);
	}

	@Override
	public List<UserDO> getProjectUser(String projectId) {
		// TODO Auto-generated method stub
		return userMapper.getProjectUser(projectId);
	}

	@Override
	public int saveUserForProject(List<Integer> userIds, String projectId) {
		// TODO Auto-generated method stub
		try {
			int result1=projectUserMapper.removeUserForProject(projectId);
			int result2=projectUserMapper.saveUserForProject(userIds, projectId);
			return 1;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}

	@Override
	public UserDO isRepeat(String userName) {
		// TODO Auto-generated method stub
		return userMapper.isRepeat(userName);
	}

//    @Override
//    public Map<String, Object> updatePersonalImg(MultipartFile file, String avatar_data, Long userId) throws Exception {
//        String fileName = file.getOriginalFilename();
//        fileName = FileUtil.renameToUUID(fileName);
//        FileDO sysFile = new FileDO(FileType.fileType(fileName), "/files/" + fileName, new Date());
//        //获取图片后缀
//        String prefix = fileName.substring((fileName.lastIndexOf(".") + 1));
//        String[] str = avatar_data.split(",");
//        //获取截取的x坐标
//        int x = (int) Math.floor(Double.parseDouble(str[0].split(":")[1]));
//        //获取截取的y坐标
//        int y = (int) Math.floor(Double.parseDouble(str[1].split(":")[1]));
//        //获取截取的高度
//        int h = (int) Math.floor(Double.parseDouble(str[2].split(":")[1]));
//        //获取截取的宽度
//        int w = (int) Math.floor(Double.parseDouble(str[3].split(":")[1]));
//        //获取旋转的角度
//        int r = Integer.parseInt(str[4].split(":")[1].replaceAll("}", ""));
//        try {
//            BufferedImage cutImage = ImageUtils.cutImage(file, x, y, w, h, prefix);
//            BufferedImage rotateImage = ImageUtils.rotateImage(cutImage, r);
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            boolean flag = ImageIO.write(rotateImage, prefix, out);
//            //转换后存入数据库
//            byte[] b = out.toByteArray();
//            FileUtil.uploadFile(b, bootdoConfig.getUploadPath(), fileName);
//        } catch (Exception e) {
//            throw new Exception("图片裁剪错误！！");
//        }
//        Map<String, Object> result = new HashMap<>();
//        if (sysFileService.save(sysFile) > 0) {
//            UserDO userDO = new UserDO();
//            userDO.setUserId(userId);
//            userDO.setPicId(sysFile.getId());
//            if (userMapper.update(userDO) > 0) {
//                result.put("url", sysFile.getUrl());
//            }
//        }
//        return result;
//    }

}
