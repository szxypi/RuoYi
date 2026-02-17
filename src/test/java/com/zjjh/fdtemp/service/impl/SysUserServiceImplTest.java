package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.FdtempApplication;
import com.zjjh.fdtemp.TestConfig;
import com.zjjh.fdtemp.beans.LoginUser;
import com.zjjh.fdtemp.beans.entity.SysRole;
import com.zjjh.fdtemp.beans.entity.SysUser;
import com.zjjh.fdtemp.common.exception.ServiceException;
import com.zjjh.fdtemp.constants.UserConstants;
import com.zjjh.fdtemp.service.SysUserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = FdtempApplication.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class SysUserServiceImplTest {

    @Autowired
    private SysUserService userService;

    private static final String ADMIN_USER_ID = "1";
    private static final String TEST_USER_ID = "2";
    private static final String NORMAL_USER_ID = "3";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        SysUser adminUser = new SysUser();
        adminUser.setId("1");
        adminUser.setLoginName("admin");
        adminUser.setUserName("超级管理员");
        adminUser.setStatus("0");
        SysRole adminRole = new SysRole();
        adminRole.setRoleKey("admin");
        adminUser.setRoles(List.of(adminRole));

        Set<String> permissions = new HashSet<>();
        permissions.add("*:*:*");
        LoginUser loginUser = new LoginUser(adminUser, permissions);

        Authentication auth = new UsernamePasswordAuthenticationToken(
            loginUser, null, loginUser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @Order(1)
    @DisplayName("1.1 查询所有用户列表")
    void testSelectUserList() {
        SysUser query = new SysUser();
        List<SysUser> list = userService.selectUserList(query);
        assertNotNull(list);
        assertTrue(list.size() >= 3);
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
    @Order(10)
    @DisplayName("2.1 校验登录名唯一性 - 已存在")
    void testCheckLoginNameUnique_Existing() {
        SysUser user = new SysUser();
        user.setLoginName("admin");
        boolean result = userService.checkLoginNameUnique(user);
        assertEquals(UserConstants.NOT_UNIQUE, result);
    }

    @Test
    @Order(11)
    @DisplayName("2.2 校验登录名唯一性 - 不存在")
    void testCheckLoginNameUnique_New() {
        SysUser user = new SysUser();
        user.setLoginName("newuser123");
        boolean result = userService.checkLoginNameUnique(user);
        assertEquals(UserConstants.UNIQUE, result);
    }

    @Test
    @Order(20)
    @DisplayName("3.1 新增用户成功")
    @Disabled("H2 不支持 MySQL 的 sysdate() 函数")
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
    @Order(30)
    @DisplayName("4.1 修改用户成功")
    @Disabled("H2 不支持 MySQL 的 sysdate() 函数")
    void testUpdateUser() {
        SysUser user = userService.selectUserById(TEST_USER_ID);
        assertNotNull(user);

        String newUserName = "修改后的用户名";
        user.setUserName(newUserName);
        user.setRoleIds(new String[]{"2"});
        user.setPostIds(new String[]{"2"});

        int result = userService.updateUser(user);
        assertTrue(result >= 0);
    }

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
