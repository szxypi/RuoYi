package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.beans.entity.SysDept;
import com.zjjh.fdtemp.beans.entity.SysRole;
import com.zjjh.fdtemp.common.annotation.DataScope;
import com.zjjh.fdtemp.common.core.domain.Ztree;
import com.zjjh.fdtemp.common.core.text.Convert;
import com.zjjh.fdtemp.common.exception.ServiceException;
import com.zjjh.fdtemp.common.utils.StringUtils;
import com.zjjh.fdtemp.common.utils.security.SecurityUtils;
import com.zjjh.fdtemp.common.utils.spring.SpringUtils;
import com.zjjh.fdtemp.constants.UserConstants;
import com.zjjh.fdtemp.dao.SysDeptDao;
import com.zjjh.fdtemp.service.SysDeptService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门管理 服务实现
 *
 * @author szx
 */
@Service
public class SysDeptServiceImpl implements SysDeptService {
    @Autowired
    private SysDeptDao deptMapper;

    @Override
    @DataScope(deptAlias = "d")
    public List<SysDept> selectDeptList(SysDept dept) {
        return deptMapper.selectDeptList(dept);
    }

    @Override
    @DataScope(deptAlias = "d")
    public List<Ztree> selectDeptTree(SysDept dept) {
        return initZtree(deptMapper.selectDeptList(dept));
    }

    @Override
    @DataScope(deptAlias = "d")
    public List<Ztree> selectDeptTreeExcludeChild(SysDept dept) {
        String excludeId = dept.getExcludeId();
        List<SysDept> depts = deptMapper.selectDeptList(dept);
        if (StringUtils.isNotEmpty(excludeId)) {
            depts.removeIf(d -> d.getId().equals(excludeId)
                    || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), excludeId));
        }
        return initZtree(depts);
    }

    @Override
    public List<Ztree> roleDeptTreeData(SysRole role) {
        List<SysDept> deptList = SpringUtils.getAopProxy(this).selectDeptList(new SysDept());
        if (StringUtils.isNull(role.getId())) {
            return initZtree(deptList);
        }
        return initZtree(deptList, deptMapper.selectRoleDeptTree(role.getId()));
    }

    @Override
    public int selectDeptCount(String parentId) {
        SysDept dept = new SysDept();
        dept.setParentId(parentId);
        return deptMapper.selectDeptCount(dept);
    }

    @Override
    public boolean checkDeptExistUser(String deptId) {
        return deptMapper.checkDeptExistUser(deptId) > 0;
    }

    @Override
    public int deleteDeptById(String deptId) {
        return deptMapper.deleteDeptById(deptId);
    }

    @Override
    public int insertDept(SysDept dept) {
        SysDept parentDept = deptMapper.selectDeptById(dept.getParentId());
        if (!UserConstants.DEPT_NORMAL.equals(parentDept.getStatus())) {
            throw new ServiceException("部门停用，不允许新增");
        }
        dept.setAncestors(parentDept.getAncestors() + "," + dept.getParentId());
        return deptMapper.insertDept(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDept(SysDept dept) {
        SysDept newParentDept = deptMapper.selectDeptById(dept.getParentId());
        SysDept oldDept = selectDeptById(dept.getId());
        if (StringUtils.isNotNull(newParentDept) && StringUtils.isNotNull(oldDept)) {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getId();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getId(), newAncestors, oldDept.getAncestors());
        }
        int result = deptMapper.updateDept(dept);
        if (UserConstants.DEPT_NORMAL.equals(dept.getStatus())
                && StringUtils.isNotEmpty(dept.getAncestors())
                && !StringUtils.equals("0", dept.getAncestors())) {
            updateParentDeptStatusNormal(dept);
        }
        return result;
    }

    @Override
    public SysDept selectDeptById(String deptId) {
        return deptMapper.selectDeptById(deptId);
    }

    @Override
    public int selectNormalChildrenDeptById(String deptId) {
        return deptMapper.selectNormalChildrenDeptById(deptId);
    }

    @Override
    public boolean checkDeptNameUnique(SysDept dept) {
        SysDept info = deptMapper.checkDeptNameUnique(dept.getDeptName(), dept.getParentId());
        return isUnique(dept.getId(), info);
    }

    @Override
    public void checkDeptDataScope(String deptId) {
        if (SecurityUtils.isAdmin() || StringUtils.isNull(deptId)) {
            return;
        }
        SysDept dept = new SysDept();
        dept.setId(deptId);
        List<SysDept> depts = SpringUtils.getAopProxy(this).selectDeptList(dept);
        if (StringUtils.isEmpty(depts)) {
            throw new ServiceException("没有权限访问部门数据！");
        }
    }

    /**
     * 对象转部门树
     */
    private List<Ztree> initZtree(List<SysDept> deptList) {
        return initZtree(deptList, null);
    }

    /**
     * 对象转部门树
     *
     * @param roleDeptList 角色已存在菜单列表
     */
    private List<Ztree> initZtree(List<SysDept> deptList, List<String> roleDeptList) {
        List<Ztree> ztrees = new ArrayList<>();
        boolean isCheck = StringUtils.isNotNull(roleDeptList);
        for (SysDept dept : deptList) {
            if (!UserConstants.DEPT_NORMAL.equals(dept.getStatus())) {
                continue;
            }
            Ztree ztree = new Ztree();
            ztree.setId(dept.getId());
            ztree.setpId(dept.getParentId());
            ztree.setName(dept.getDeptName());
            ztree.setTitle(dept.getDeptName());
            if (isCheck) {
                ztree.setChecked(roleDeptList.contains(dept.getId() + dept.getDeptName()));
            }
            ztrees.add(ztree);
        }
        return ztrees;
    }

    /**
     * 修改该部门的父级部门状态
     */
    private void updateParentDeptStatusNormal(SysDept dept) {
        String[] deptIds = Convert.toStrArray(dept.getAncestors());
        deptMapper.updateDeptStatusNormal(deptIds);
    }

    /**
     * 修改子元素关系
     *
     * @param deptId       被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    private void updateDeptChildren(String deptId, String newAncestors, String oldAncestors) {
        List<SysDept> children = deptMapper.selectChildrenDeptById(deptId);
        if (children.isEmpty()) {
            return;
        }
        for (SysDept child : children) {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }
        deptMapper.updateDeptChildren(children);
    }

    /**
     * 判断是否唯一
     */
    private boolean isUnique(String currentId, SysDept existing) {
        String deptId = StringUtils.isNull(currentId) ? "" : currentId;
        return StringUtils.isNull(existing) || existing.getId().equals(deptId)
                ? UserConstants.UNIQUE
                : UserConstants.NOT_UNIQUE;
    }
}
