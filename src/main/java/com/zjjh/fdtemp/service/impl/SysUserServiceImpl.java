package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.beans.entity.*;
import com.zjjh.fdtemp.common.annotation.DataScope;
import com.zjjh.fdtemp.common.core.text.Convert;
import com.zjjh.fdtemp.common.exception.ServiceException;
import com.zjjh.fdtemp.common.utils.ExceptionUtil;
import com.zjjh.fdtemp.common.utils.StringUtils;
import com.zjjh.fdtemp.common.utils.bean.BeanValidators;
import com.zjjh.fdtemp.common.utils.html.EscapeUtil;
import com.zjjh.fdtemp.common.utils.security.SecurityUtils;
import com.zjjh.fdtemp.common.utils.spring.SpringUtils;
import com.zjjh.fdtemp.constants.UserConstants;
import com.zjjh.fdtemp.dao.*;
import com.zjjh.fdtemp.service.SysConfigService;
import com.zjjh.fdtemp.service.SysDeptService;
import com.zjjh.fdtemp.service.SysUserService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户 业务层处理
 *
 * @author szx
 */
@Service
public class SysUserServiceImpl implements SysUserService {
    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    private SysUserDao userMapper;

    @Autowired
    private SysRoleDao roleMapper;

    @Autowired
    private SysPostDao postMapper;

    @Autowired
    private SysUserPostDao userPostMapper;

    @Autowired
    private SysUserRoleDao userRoleMapper;

    @Autowired
    private SysConfigService configService;

    @Autowired
    private SysDeptService deptService;

    @Autowired
    protected Validator validator;

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectUserList(SysUser user) {
        return userMapper.selectUserList(user);
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectAllocatedList(SysUser user) {
        return userMapper.selectAllocatedList(user);
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectUnallocatedList(SysUser user) {
        return userMapper.selectUnallocatedList(user);
    }

    @Override
    public SysUser selectUserByLoginName(String userName) {
        return userMapper.selectUserByLoginName(userName);
    }

    @Override
    public SysUser selectUserByPhoneNumber(String phoneNumber) {
        return userMapper.selectUserByPhoneNumber(phoneNumber);
    }

    @Override
    public SysUser selectUserByEmail(String email) {
        return userMapper.selectUserByEmail(email);
    }

    @Override
    public SysUser selectUserById(String userId) {
        return userMapper.selectUserById(userId);
    }

    @Override
    public List<SysUserRole> selectUserRoleByUserId(String userId) {
        return userRoleMapper.selectUserRoleByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserById(String userId) {
        userRoleMapper.deleteUserRoleByUserId(userId);
        userPostMapper.deleteUserPostByUserId(userId);
        return userMapper.deleteUserById(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserByIds(String ids) {
        String[] userIds = Convert.toStrArray(ids);
        for (String userId : userIds) {
            checkUserAllowed(new SysUser(userId));
            checkUserDataScope(userId);
        }
        userRoleMapper.deleteUserRole(userIds);
        userPostMapper.deleteUserPost(userIds);
        return userMapper.deleteUserByIds(userIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertUser(SysUser user) {
        int rows = userMapper.insertUser(user);
        insertUserPost(user);
        insertUserRole(user.getId(), user.getRoleIds());
        return rows;
    }

    @Override
    public boolean registerUser(SysUser user) {
        user.setUserType(UserConstants.REGISTER_USER_TYPE);
        return userMapper.insertUser(user) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateUser(SysUser user) {
        String userId = user.getId();
        userRoleMapper.deleteUserRoleByUserId(userId);
        insertUserRole(userId, user.getRoleIds());
        userPostMapper.deleteUserPostByUserId(userId);
        insertUserPost(user);
        return userMapper.updateUser(user);
    }

    @Override
    public int updateUserInfo(SysUser user) {
        return userMapper.updateUser(user);
    }

    public boolean updateUserAvatar(String userId, String avatar) {
        return userMapper.updateUserAvatar(userId, avatar) > 0;
    }

    public void updateLoginInfo(String userId, String loginIp, Date loginDate) {
        userMapper.updateLoginInfo(userId, loginIp, loginDate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUserAuth(String userId, String[] roleIds) {
        userRoleMapper.deleteUserRoleByUserId(userId);
        insertUserRole(userId, roleIds);
    }

    @Override
    public int resetUserPwd(SysUser user) {
        return userMapper.resetUserPwd(user.getId(), user.getPassword());
    }

    @Override
    public boolean checkLoginNameUnique(SysUser user) {
        return isUnique(user.getId(), userMapper.checkLoginNameUnique(user.getLoginName()));
    }

    @Override
    public boolean checkPhoneUnique(SysUser user) {
        return isUnique(user.getId(), userMapper.checkPhoneUnique(user.getPhonenumber()));
    }

    @Override
    public boolean checkEmailUnique(SysUser user) {
        return isUnique(user.getId(), userMapper.checkEmailUnique(user.getEmail()));
    }

    @Override
    public void checkUserAllowed(SysUser user) {
        if (StringUtils.isNull(user) || StringUtils.isEmpty(user.getId())) {
            return;
        }
        SysUser dbUser = userMapper.selectUserById(user.getId());
        if (dbUser != null && dbUser.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    @Override
    public void checkUserDataScope(String userId) {
        if (SecurityUtils.isAdmin()) {
            return;
        }
        SysUser user = new SysUser();
        user.setId(userId);
        List<SysUser> users = SpringUtils.getAopProxy(this).selectUserList(user);
        if (StringUtils.isEmpty(users)) {
            throw new ServiceException("没有权限访问用户数据！");
        }
    }

    @Override
    public String selectUserRoleGroup(String userId) {
        List<SysRole> list = roleMapper.selectRolesByUserId(userId);
        if (CollectionUtils.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysRole::getRoleName).collect(Collectors.joining(","));
    }

    @Override
    public String selectUserPostGroup(String userId) {
        List<SysPost> list = postMapper.selectPostsByUserId(userId);
        if (CollectionUtils.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysPost::getPostName).collect(Collectors.joining(","));
    }

    @Override
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName) {
        if (CollectionUtils.isEmpty(userList)) {
            throw new ServiceException("导入用户数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (SysUser user : userList) {
            try {
                SysUser existingUser = userMapper.selectUserByLoginName(user.getLoginName());
                if (StringUtils.isNull(existingUser)) {
                    importNewUser(user, operName);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、账号 ")
                            .append(user.getLoginName()).append(" 导入成功");
                } else if (isUpdateSupport) {
                    updateUserFromImport(user, existingUser, operName);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、账号 ")
                            .append(user.getLoginName()).append(" 更新成功");
                } else {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、账号 ")
                            .append(user.getLoginName()).append(" 已存在");
                }
            } catch (Exception e) {
                handleImportFailure(user, e, failureNum++, failureMsg);
            }
        }
        return buildImportResult(successNum, failureNum, successMsg, failureMsg);
    }

    @Override
    public int changeStatus(SysUser user) {
        return userMapper.updateUserStatus(user.getId(), user.getStatus());
    }

    /**
     * 新增用户角色信息
     */
    private void insertUserRole(String userId, String[] roleIds) {
        if (StringUtils.isEmpty(roleIds)) {
            return;
        }
        List<SysUserRole> list = Arrays.stream(roleIds)
                .map(roleId -> {
                    SysUserRole ur = new SysUserRole();
                    ur.setUserId(userId);
                    ur.setRoleId(roleId);
                    return ur;
                })
                .collect(Collectors.toList());
        userRoleMapper.batchUserRole(list);
    }

    /**
     * 新增用户岗位信息
     */
    private void insertUserPost(SysUser user) {
        String[] posts = user.getPostIds();
        if (StringUtils.isEmpty(posts)) {
            return;
        }
        List<SysUserPost> list = Arrays.stream(posts)
                .map(postId -> {
                    SysUserPost up = new SysUserPost();
                    up.setUserId(user.getId());
                    up.setPostId(postId);
                    return up;
                })
                .collect(Collectors.toList());
        userPostMapper.batchUserPost(list);
    }

    /**
     * 判断字段是否唯一（通用校验逻辑）
     */
    private boolean isUnique(String userId, SysUser existUser) {
        String id = StringUtils.isNull(userId) ? "" : userId;
        return StringUtils.isNull(existUser) || existUser.getId().equals(id)
                ? UserConstants.UNIQUE
                : UserConstants.NOT_UNIQUE;
    }

    /**
     * 导入新用户
     */
    private void importNewUser(SysUser user, String operName) {
        BeanValidators.validateWithException(validator, user);
        deptService.checkDeptDataScope(user.getDeptId());
        String password = configService.selectConfigByKey("sys.user.initPassword");
        user.setPassword(SecurityUtils.encryptPassword(password));
        user.setCreateUser(operName);
        userMapper.insertUser(user);
    }

    /**
     * 更新导入的用户
     */
    private void updateUserFromImport(SysUser user, SysUser existingUser, String operName) {
        BeanValidators.validateWithException(validator, user);
        checkUserAllowed(existingUser);
        checkUserDataScope(existingUser.getId());
        deptService.checkDeptDataScope(user.getDeptId());
        user.setId(existingUser.getId());
        user.setDeptId(existingUser.getDeptId());
        user.setUpdateUser(operName);
        userMapper.updateUser(user);
    }

    /**
     * 处理导入失败
     */
    private void handleImportFailure(SysUser user, Exception e, int failureNum, StringBuilder failureMsg) {
        String loginName = user.getLoginName();
        if (ExceptionUtil.isCausedBy(e, ConstraintViolationException.class)) {
            loginName = EscapeUtil.clean(loginName);
        }
        String msg = "<br/>" + failureNum + "、账号 " + loginName + " 导入失败：";
        failureMsg.append(msg).append(e.getMessage());
        log.error(msg, e);
    }

    /**
     * 构建导入结果
     */
    private String buildImportResult(int successNum, int failureNum,
                                     StringBuilder successMsg, StringBuilder failureMsg) {
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        }
        successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        return successMsg.toString();
    }
}
