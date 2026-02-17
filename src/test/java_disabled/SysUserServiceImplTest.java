package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.FdtempApplication;
import com.zjjh.fdtemp.TestConfig;
import com.zjjh.fdtemp.beans.entity.SysDept;
import com.zjjh.fdtemp.beans.entity.SysRole;
import com.zjjh.fdtemp.beans.entity.SysUser;
import com.zjjh.fdtemp.common.exception.ServiceException;
import com.zjjh.fdtemp.constants.UserConstants;
import com.zjjh.fdtemp.service.SysDeptService;
import com.zjjh.fdtemp.service.SysUserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SysUserService 单元测试类
 */
@SpringBootTest(classes = FdtempApplication.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class SysUserServiceImplTest {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysDeptService deptService;

    private static final String ADMIN_USER_ID = "1";
    private static final String TEST_USER_ID = "2";
    private static final String NORMAL_USER_ID = "3";

    // ============================================
    // 查询测试
    // ============================================

    @Test
    @Order(1)
    @DisplayName("1.1 查询所有用户列表")
    void testSelectUserList() {
        SysUser query = new SysUser();
        List<SysUser> list = userService.selectUserList(query);
        assertNotNull(list);
        assertTrue(list.size() >= 3, "应该至少有3个测试用户");
    }

    @Test
    @Order(2)
    @DisplayName("1.2 根据登录名查询用户")
    void testSelectUserByLoginName() {
        SysUser user = userService.selectUserByLoginName("admin");
        assertNotNull(user);
        assertEquals("admin", user.getLoginName());
        assertEquals("超级管理员", user.getUserName());
    }

    @Test
    @Order(3)
    @DisplayName("1.3 根据不存在的登录名查询用户")
    void testSelectUserByLoginNameNotFound() {
        SysUser user = userService.selectUserByLoginName("nonexistent");
        assertNull(user);
    }

    @Test
    @Order(4)
    @DisplayName("1.4 根据ID查询用户")
    void testSelectUserById() {
        SysUser user = userService.selectUserById(ADMIN_USER_ID);
        assertNotNull(user);
        assertEquals("admin", user.getLoginName());
    }

    @Test
    @Order(5)
    @DisplayName("1.5 根据手机号查询用户")
    void testSelectUserByPhoneNumber() {
        SysUser user = userService.selectUserByPhoneNumber("15888888888");
        assertNotNull(user);
        assertEquals("admin", user.getLoginName());
    }

    @Test
    @Order(6)
    @DisplayName("1.6 根据邮箱查询用户")
    void testSelectUserByEmail() {
        SysUser user = userService.selectUserByEmail("admin@fdtemp.com");
        assertNotNull(user);
        assertEquals("admin", user.getLoginName());
    }

    @Test
    @Order(7)
    @DisplayName("1.7 查询已分配角色用户列表")
    void testSelectAllocatedList() {
        SysUser query = new SysUser();
        query.setRoleId("1"); // admin角色
        List<SysUser> list = userService.selectAllocatedList(query);
        assertNotNull(list);
    }

    @Test
    @Order(8)
    @DisplayName("1.8 查询未分配角色用户列表")
    void testSelectUnallocatedList() {
        SysUser query = new SysUser();
        query.setRoleId("2"); // 普通角色
        List<SysUser> list = userService.selectUnallocatedList(query);
        assertNotNull(list);
    }

    // ============================================
    // 唯一性校验测试
    // ============================================

    @Test
    @Order(10)
    @DisplayName("2.1 校验登录名唯一性 - 新增时已存在")
    void testCheckLoginNameUnique_Existing() {
        SysUser user = new SysUser();
        user.setLoginName("admin");
        boolean result = userService.checkLoginNameUnique(user);
        assertEquals(UserConstants.NOT_UNIQUE, result);
    }

    @Test
    @Order(11)
    @DisplayName("2.2 校验登录名唯一性 - 新增时不存在")
    void testCheckLoginNameUnique_New() {
        SysUser user = new SysUser();
        user.setLoginName("newuser123");
        boolean result = userService.checkLoginNameUnique(user);
        assertEquals(UserConstants.UNIQUE, result);
    }

    @Test
    @Order(12)
    @DisplayName("2.3 校验登录名唯一性 - 修改时保持自己的名字")
    void testCheckLoginNameUnique_Self() {
        SysUser user = new SysUser();
        user.setId(ADMIN_USER_ID);
        user.setLoginName("admin");
        boolean result = userService.checkLoginNameUnique(user);
        assertEquals(UserConstants.UNIQUE, result);
    }

    @Test
    @Order(13)
    @DisplayName("2.4 校验手机号唯一性 - 已存在")
    void testCheckPhoneUnique_Existing() {
        SysUser user = new SysUser();
        user.setPhonenumber("15888888888");
        boolean result = userService.checkPhoneUnique(user);
        assertEquals(UserConstants.NOT_UNIQUE, result);
    }

    @Test
    @Order(14)
    @DisplayName("2.5 校验手机号唯一性 - 不存在")
    void testCheckPhoneUnique_New() {
        SysUser user = new SysUser();
        user.setPhonenumber("19999999999");
        boolean result = userService.checkPhoneUnique(user);
        assertEquals(UserConstants.UNIQUE, result);
    }

    @Test
    @Order(15)
    @DisplayName("2.6 校验邮箱唯一性 - 已存在")
    void testCheckEmailUnique_Existing() {
        SysUser user = new SysUser();
        user.setEmail("admin@fdtemp.com");
        boolean result = userService.checkEmailUnique(user);
        assertEquals(UserConstants.NOT_UNIQUE, result);
    }

    @Test
    @Order(16)
    @DisplayName("2.7 校验邮箱唯一性 - 不存在")
    void testCheckEmailUnique_New() {
        SysUser user = new SysUser();
        user.setEmail("newuser@test.com");
        boolean result = userService.checkEmailUnique(user);
        assertEquals(UserConstants.UNIQUE, result);
    }

    // ============================================
    // 新增测试
    // ============================================

    @Test
    @Order(20)
    @DisplayName("3.1 新增用户成功")
    void testInsertUser() {
        SysUser user = createTestUser("testinsert", "测试新增用户");
        user.setDeptId("2");
        user.setRoleIds(new String[]{"2"});
        user.setPostIds(new String[]{"2"});

        int result = userService.insertUser(user);
        assertTrue(result > 0);

        SysUser saved = userService.selectUserByLoginName("testinsert");
        assertNotNull(saved);
        assertEquals("测试新增用户", saved.getUserName());
    }

    @Test
    @Order(21)
    @DisplayName("3.2 注册用户成功")
    void testRegisterUser() {
        SysUser user = new SysUser();
        user.setLoginName("registertest");
        user.setUserName("注册测试用户");
        user.setPassword("test123");
        user.setDeptId("2");

        boolean result = userService.registerUser(user);
        assertTrue(result);

        SysUser saved = userService.selectUserByLoginName("registertest");
        assertNotNull(saved);
        assertEquals(UserConstants.REGISTER_USER_TYPE, saved.getUserType());
    }

    // ============================================
    // 修改测试
    // ============================================

    @Test
    @Order(30)
    @DisplayName("4.1 修改用户成功")
    void testUpdateUser() {
        SysUser user = userService.selectUserById(TEST_USER_ID);
        assertNotNull(user);

        String newUserName = "修改后的用户名";
        user.setUserName(newUserName);
        user.setRoleIds(new String[]{"2"});
        user.setPostIds(new String[]{"2"});

        int result = userService.updateUser(user);
        assertTrue(result >= 0);

        SysUser updated = userService.selectUserById(TEST_USER_ID);
        assertEquals(newUserName, updated.getUserName());
    }

    @Test
    @Order(31)
    @DisplayName("4.2 修改用户状态")
    void testChangeStatus() {
        SysUser user = new SysUser();
        user.setId(TEST_USER_ID);
        user.setStatus("1"); // 停用

        int result = userService.changeStatus(user);
        assertTrue(result >= 0);
    }

    @Test
    @Order(32)
    @DisplayName("4.3 重置用户密码")
    void testResetUserPwd() {
        SysUser user = new SysUser();
        user.setId(TEST_USER_ID);
        user.setPassword("newPassword123");

        int result = userService.resetUserPwd(user);
        assertTrue(result >= 0);
    }

    @Test
    @Order(33)
    @DisplayName("4.4 更新用户头像")
    void testUpdateUserAvatar() {
        boolean result = userService.updateUserAvatar(TEST_USER_ID, "/avatar/test.png");
        assertTrue(result);
    }

    @Test
    @Order(34)
    @DisplayName("4.5 更新用户登录信息")
    void testUpdateLoginInfo() {
        assertDoesNotThrow(() -> {
            userService.updateLoginInfo(TEST_USER_ID, "127.0.0.1", new Date());
        });
    }

    // ============================================
    // 删除测试
    // ============================================

    @Test
    @Order(40)
    @DisplayName("5.1 批量删除用户")
    void testDeleteUserByIds() {
        // 先新增一个用户用于删除
        SysUser user = createTestUser("tobedeleted", "待删除用户");
        user.setDeptId("2");
        userService.insertUser(user);

        SysUser saved = userService.selectUserByLoginName("tobedeleted");
        assertNotNull(saved);

        // 测试用户ID不是admin(1)，可以删除
        int result = userService.deleteUserByIds(saved.getId());
        assertTrue(result >= 0);
    }

    @Test
    @Order(41)
    @DisplayName("5.2 删除单个用户")
    void testDeleteUserById() {
        // 先新增一个用户用于删除
        SysUser user = createTestUser("tobedeleted2", "待删除用户2");
        user.setDeptId("2");
        userService.insertUser(user);

        SysUser saved = userService.selectUserByLoginName("tobedeleted2");
        assertNotNull(saved);

        int result = userService.deleteUserById(saved.getId());
        assertTrue(result >= 0);
    }

    // ============================================
    // 权限校验测试
    // ============================================

    @Test
    @Order(50)
    @DisplayName("6.1 校验管理员用户是否允许操作 - 不允许")
    void testCheckUserAllowed_Admin() {
        SysUser adminUser = new SysUser();
        adminUser.setId("1");
        adminUser.setLoginName("admin");

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.checkUserAllowed(adminUser);
        });
        assertTrue(exception.getMessage().contains("超级管理员"));
    }

    @Test
    @Order(51)
    @DisplayName("6.2 校验普通用户是否允许操作 - 允许")
    void testCheckUserAllowed_Normal() {
        SysUser normalUser = new SysUser();
        normalUser.setId("2");
        normalUser.setLoginName("test");

        assertDoesNotThrow(() -> {
            userService.checkUserAllowed(normalUser);
        });
    }

    // ============================================
    // 角色岗位组测试
    // ============================================

    @Test
    @Order(60)
    @DisplayName("7.1 查询用户角色组")
    void testSelectUserRoleGroup() {
        String roleGroup = userService.selectUserRoleGroup(ADMIN_USER_ID);
        assertNotNull(roleGroup);
        assertTrue(roleGroup.contains("超级管理员"));
    }

    @Test
    @Order(61)
    @DisplayName("7.2 查询用户岗位组")
    void testSelectUserPostGroup() {
        String postGroup = userService.selectUserPostGroup(ADMIN_USER_ID);
        assertNotNull(postGroup);
        assertTrue(postGroup.contains("董事长"));
    }

    @Test
    @Order(62)
    @DisplayName("7.3 用户授权角色")
    void testInsertUserAuth() {
        assertDoesNotThrow(() -> {
            userService.insertUserAuth(NORMAL_USER_ID, new String[]{"2"});
        });
    }

    // ============================================
    // 导入测试
    // ============================================

    @Test
    @Order(70)
    @DisplayName("8.1 导入空用户列表 - 抛出异常")
    void testImportUser_EmptyList() {
        assertThrows(ServiceException.class, () -> {
            userService.importUser(null, false, "admin");
        });
    }

    // ============================================
    // 辅助方法
    // ============================================

    private SysUser createTestUser(String loginName, String userName) {
        SysUser user = new SysUser();
        user.setLoginName(loginName);
        user.setUserName(userName);
        user.setPassword("test123");
        user.setEmail(loginName + "@test.com");
        user.setPhonenumber("13800000000");
        user.setSex("0");
        user.setStatus("0");
        user.setUserType("00");
        user.setCreateUser("test");
        user.setCreateTime(new Date());
        return user;
    }
}
