package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.beans.entity.SysRole;
import com.zjjh.fdtemp.beans.entity.SysRoleDept;
import com.zjjh.fdtemp.beans.entity.SysRoleMenu;
import com.zjjh.fdtemp.beans.entity.SysUserRole;
import com.zjjh.fdtemp.common.annotation.DataScope;
import com.zjjh.fdtemp.common.core.text.Convert;
import com.zjjh.fdtemp.common.exception.ServiceException;
import com.zjjh.fdtemp.common.utils.StringUtils;
import com.zjjh.fdtemp.common.utils.security.SecurityUtils;
import com.zjjh.fdtemp.common.utils.spring.SpringUtils;
import com.zjjh.fdtemp.constants.UserConstants;
import com.zjjh.fdtemp.dao.SysRoleDao;
import com.zjjh.fdtemp.dao.SysRoleDeptDao;
import com.zjjh.fdtemp.dao.SysRoleMenuDao;
import com.zjjh.fdtemp.dao.SysUserRoleDao;
import com.zjjh.fdtemp.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色 业务层处理
 *
 * @author szx
 */
@Service
public class SysRoleServiceImpl implements SysRoleService {
    @Autowired
    private SysRoleDao roleMapper;

    @Autowired
    private SysRoleMenuDao roleMenuMapper;

    @Autowired
    private SysUserRoleDao userRoleMapper;

    @Autowired
    private SysRoleDeptDao roleDeptMapper;

    @Override
    @DataScope(deptAlias = "d")
    public List<SysRole> selectRoleList(SysRole role) {
        return roleMapper.selectRoleList(role);
    }

    @Override
    public Set<String> selectRoleKeys(String userId) {
        List<SysRole> perms = roleMapper.selectRolesByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (SysRole perm : perms) {
            if (StringUtils.isNotNull(perm)) {
                permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
            }
        }
        return permsSet;
    }

    @Override
    public List<SysRole> selectRolesByUserId(String userId) {
        List<SysRole> userRoles = roleMapper.selectRolesByUserId(userId);
        Set<String> userRoleIds = userRoles.stream()
                .map(SysRole::getId)
                .collect(Collectors.toSet());
        List<SysRole> roles = selectRoleAll();
        roles.stream()
                .filter(role -> userRoleIds.contains(role.getId()))
                .forEach(role -> role.setFlag(true));
        return roles;
    }

    @Override
    public List<SysRole> selectRoleAll() {
        return SpringUtils.getAopProxy(this).selectRoleList(new SysRole());
    }

    @Override
    public SysRole selectRoleById(String roleId) {
        return roleMapper.selectRoleById(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRoleById(String roleId) {
        roleMenuMapper.deleteRoleMenuByRoleId(roleId);
        roleDeptMapper.deleteRoleDeptByRoleId(roleId);
        return roleMapper.deleteRoleById(roleId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRoleByIds(String ids) {
        String[] roleIds = Convert.toStrArray(ids);
        for (String roleId : roleIds) {
            checkRoleAllowed(new SysRole(roleId));
            checkRoleDataScope(roleId);
            if (countUserRoleByRoleId(roleId) > 0) {
                SysRole role = selectRoleById(roleId);
                throw new ServiceException(String.format("%s已分配,不能删除", role.getRoleName()));
            }
        }
        roleMenuMapper.deleteRoleMenu(roleIds);
        roleDeptMapper.deleteRoleDept(roleIds);
        return roleMapper.deleteRoleByIds(roleIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRole(SysRole role) {
        roleMapper.insertRole(role);
        return insertRoleMenu(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRole(SysRole role) {
        roleMapper.updateRole(role);
        roleMenuMapper.deleteRoleMenuByRoleId(role.getId());
        return insertRoleMenu(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int authDataScope(SysRole role) {
        roleMapper.updateRole(role);
        roleDeptMapper.deleteRoleDeptByRoleId(role.getId());
        return insertRoleDept(role);
    }

    @Override
    public boolean checkRoleNameUnique(SysRole role) {
        SysRole info = roleMapper.checkRoleNameUnique(role.getRoleName());
        return isUnique(role.getId(), info);
    }

    @Override
    public boolean checkRoleKeyUnique(SysRole role) {
        SysRole info = roleMapper.checkRoleKeyUnique(role.getRoleKey());
        return isUnique(role.getId(), info);
    }

    @Override
    public void checkRoleAllowed(SysRole role) {
        if (StringUtils.isNull(role) || StringUtils.isEmpty(role.getId())) {
            return;
        }
        SysRole dbRole = roleMapper.selectRoleById(role.getId());
        if (dbRole != null && dbRole.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员角色");
        }
    }

    @Override
    public void checkRoleDataScope(String... roleIds) {
        if (SecurityUtils.isAdmin()) {
            return;
        }
        for (String roleId : roleIds) {
            SysRole role = new SysRole();
            role.setId(roleId);
            List<SysRole> roles = SpringUtils.getAopProxy(this).selectRoleList(role);
            if (StringUtils.isEmpty(roles)) {
                throw new ServiceException("没有权限访问角色数据！");
            }
        }
    }

    @Override
    public int countUserRoleByRoleId(String roleId) {
        return userRoleMapper.countUserRoleByRoleId(roleId);
    }

    @Override
    public int changeStatus(SysRole role) {
        return roleMapper.updateRole(role);
    }

    @Override
    public int deleteAuthUser(SysUserRole userRole) {
        return userRoleMapper.deleteUserRoleInfo(userRole);
    }

    @Override
    public int deleteAuthUsers(String roleId, String userIds) {
        return userRoleMapper.deleteUserRoleInfos(roleId, Convert.toStrArray(userIds));
    }

    @Override
    public int insertAuthUsers(String roleId, String userIds) {
        String[] users = Convert.toStrArray(userIds);
        List<SysUserRole> list = Arrays.stream(users)
                .map(userId -> {
                    SysUserRole ur = new SysUserRole();
                    ur.setUserId(userId);
                    ur.setRoleId(roleId);
                    return ur;
                })
                .collect(Collectors.toList());
        return userRoleMapper.batchUserRole(list);
    }

    /**
     * 新增角色菜单信息
     */
    private int insertRoleMenu(SysRole role) {
        String[] menuIds = role.getMenuIds();
        if (StringUtils.isEmpty(menuIds)) {
            return 1;
        }
        List<SysRoleMenu> list = Arrays.stream(menuIds)
                .map(menuId -> {
                    SysRoleMenu rm = new SysRoleMenu();
                    rm.setRoleId(role.getId());
                    rm.setMenuId(menuId);
                    return rm;
                })
                .collect(Collectors.toList());
        return roleMenuMapper.batchRoleMenu(list);
    }

    /**
     * 新增角色部门信息(数据权限)
     */
    private int insertRoleDept(SysRole role) {
        String[] deptIds = role.getDeptIds();
        if (StringUtils.isEmpty(deptIds)) {
            return 1;
        }
        List<SysRoleDept> list = Arrays.stream(deptIds)
                .map(deptId -> {
                    SysRoleDept rd = new SysRoleDept();
                    rd.setRoleId(role.getId());
                    rd.setDeptId(deptId);
                    return rd;
                })
                .collect(Collectors.toList());
        return roleDeptMapper.batchRoleDept(list);
    }

    /**
     * 判断是否唯一
     */
    private boolean isUnique(String currentId, SysRole existing) {
        String roleId = StringUtils.isNull(currentId) ? "" : currentId;
        return StringUtils.isNull(existing) || existing.getId().equals(roleId)
                ? UserConstants.UNIQUE
                : UserConstants.NOT_UNIQUE;
    }
}
