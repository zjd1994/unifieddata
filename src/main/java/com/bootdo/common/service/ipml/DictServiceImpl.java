package com.bootdo.common.service.ipml;

import com.bootdo.common.utils.BuildTree;
import com.bootdo.common.utils.StringUtils;
import com.bootdo.system.domain.DeptDO;
import com.bootdo.system.domain.UserDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.bootdo.common.dao.DictDao;
import com.bootdo.common.domain.DictDO;
import com.bootdo.common.domain.Tree;
import com.bootdo.common.service.DictService;


@Service
public class DictServiceImpl implements DictService {
    @Autowired
    private DictDao dictDao;

    @Override
    public DictDO get(Long id) {
        return dictDao.get(id);
    }

    @Override
    public List<DictDO> list(Map<String, Object> map) {
        return dictDao.list(map);
    }

    @Override
    public int count(Map<String, Object> map) {
        return dictDao.count(map);
    }

    @Override
    public int save(DictDO dict) {
        return dictDao.save(dict);
    }

    @Override
    public int update(DictDO dict) {
        return dictDao.update(dict);
    }

    @Override
    public int remove(Long id) {
        return dictDao.remove(id);
    }

    @Override
    public int batchRemove(Long[] ids) {
        return dictDao.batchRemove(ids);
    }

    @Override

    public List<DictDO> listType() {
        return dictDao.listType();
    }

    @Override
    public String getName(String type, String value) {
        Map<String, Object> param = new HashMap<String, Object>(16);
        param.put("type", type);
        param.put("value", value);
        String rString = dictDao.list(param).get(0).getName();
        return rString;
    }

    @Override
    public List<DictDO> getHobbyList(UserDO userDO) {
        Map<String, Object> param = new HashMap<>(16);
        param.put("type", "hobby");
        List<DictDO> hobbyList = dictDao.list(param);

        if (StringUtils.isNotEmpty(userDO.getHobby())) {
            String userHobbys[] = userDO.getHobby().split(";");
            for (String userHobby : userHobbys) {
                for (DictDO hobby : hobbyList) {
                    if (!Objects.equals(userHobby, hobby.getId().toString())) {
                        continue;
                    }
                    hobby.setRemarks("true");
                    break;
                }
            }
        }

        return hobbyList;
    }

    @Override
    public List<DictDO> getSexList() {
        Map<String, Object> param = new HashMap<>(16);
        param.put("type", "sex");
        return dictDao.list(param);
    }

    @Override
    public List<DictDO> listByType(String type) {
        Map<String, Object> param = new HashMap<>(16);
        param.put("type", type);
        return dictDao.list(param);
    }
    /*获得数据字典页面的树*/
   	@Override
   	public Tree<DeptDO> getTree() {
   		// TODO Auto-generated method stub
   		List<Tree<DeptDO>> trees = new ArrayList<Tree<DeptDO>>();
   		List<DictDO> sysDepts = dictDao.listType();
   		for (DictDO sysDept : sysDepts) {
   			Tree<DeptDO> tree = new Tree<DeptDO>();
   			tree.setId(sysDept.getType());
   			System.out.println(sysDept.getType());
   			tree.setParentId("0");
   			tree.setText(sysDept.getDescription());
   			System.out.println(sysDept.getDescription());
   			Map<String, Object> state = new HashMap<>(16);
   			state.put("opened", true);
   			tree.setState(state);
   			trees.add(tree);
   		}
   		// 默认顶级菜单为０，根据数据库实际情况调整
   		Tree<DeptDO> t = BuildTree.build(trees);
   		return t;
   	}

}
