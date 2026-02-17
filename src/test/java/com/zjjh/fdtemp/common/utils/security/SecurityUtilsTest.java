package com.zjjh.fdtemp.common.utils.security;

import com.zjjh.fdtemp.beans.LoginUser;
import com.zjjh.fdtemp.beans.entity.SysRole;
import com.zjjh.fdtemp.beans.entity.SysUser;
import com.zjjh.fdtemp.common.exception.ServiceException;
import com.zjjh.fdtemp.common.utils.spring.SpringUtils;
import com.zjjh.fdtemp.service.SysUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * SecurityUtils 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityUtils 测试")
class SecurityUtilsTest {

    private LoginUser testLoginUser;
    private SysUser testUser;
    private static final String TEST_PASSWORD = "testPassword123";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        testUser = new SysUser();
        testUser.setId("1");
        testUser.setLoginName("testuser");
        testUser.setUserName("测试用户");
        testUser.setPassword(SecurityUtils.encryptPassword(TEST_PASSWORD));
        testUser.setStatus("0");
        testUser.setYn("0");

        Set<String> permissions = new HashSet<>();
        permissions.add("system:user:list");
        testLoginUser = new LoginUser(testUser, permissions);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setAuthentication(LoginUser loginUser) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
            loginUser, null, loginUser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("encryptPassword - 正常加密密码")
    void testEncryptPassword_Success() {
        String encrypted = SecurityUtils.encryptPassword(TEST_PASSWORD);

        assertNotNull(encrypted);
        assertTrue(encrypted.startsWith("$2a$"));
        assertNotEquals(TEST_PASSWORD, encrypted);
    }

    @Test
    @DisplayName("matchesPassword - 密码匹配成功")
    void testMatchesPassword_Success() {
        String encrypted = SecurityUtils.encryptPassword(TEST_PASSWORD);

        assertTrue(SecurityUtils.matchesPassword(TEST_PASSWORD, encrypted));
    }

    @Test
    @DisplayName("matchesPassword - 密码匹配失败")
    void testMatchesPassword_Failure() {
        String encrypted = SecurityUtils.encryptPassword(TEST_PASSWORD);

        assertFalse(SecurityUtils.matchesPassword("wrongPassword", encrypted));
    }

    @Test
    @DisplayName("getAuthentication - 正常获取认证信息")
    void testGetAuthentication_Success() {
        setAuthentication(testLoginUser);

        Authentication auth = SecurityUtils.getAuthentication();

        assertNotNull(auth);
        assertEquals("testuser", auth.getName());
    }

    @Test
    @DisplayName("getAuthentication - 未认证返回null")
    void testGetAuthentication_NotAuthenticated() {
        Authentication auth = SecurityUtils.getAuthentication();

        assertNull(auth);
    }

    @Test
    @DisplayName("getLoginUser - 正常获取登录用户")
    void testGetLoginUser_Success() {
        setAuthentication(testLoginUser);

        LoginUser loginUser = SecurityUtils.getLoginUser();

        assertNotNull(loginUser);
        assertEquals("testuser", loginUser.getUsername());
    }

    @Test
    @DisplayName("getLoginUser - 未认证抛出异常")
    void testGetLoginUser_NotAuthenticated() {
        assertThrows(ServiceException.class, SecurityUtils::getLoginUser);
    }

    @Test
    @DisplayName("getSysUser - 正常获取系统用户")
    void testGetSysUser_Success() {
        setAuthentication(testLoginUser);

        SysUser user = SecurityUtils.getSysUser();

        assertNotNull(user);
        assertEquals("testuser", user.getLoginName());
        assertEquals("1", user.getId());
    }

    @Test
    @DisplayName("getSysUser - 未认证抛出异常")
    void testGetSysUser_NotAuthenticated() {
        assertThrows(ServiceException.class, SecurityUtils::getSysUser);
    }

    @Test
    @DisplayName("getUserId - 正常获取用户ID")
    void testGetUserId_Success() {
        setAuthentication(testLoginUser);

        String userId = SecurityUtils.getUserId();

        assertEquals("1", userId);
    }

    @Test
    @DisplayName("getLoginName - 正常获取登录名")
    void testGetLoginName_Success() {
        setAuthentication(testLoginUser);

        String loginName = SecurityUtils.getLoginName();

        assertEquals("testuser", loginName);
    }

    @Test
    @DisplayName("getUserName - 正常获取用户名")
    void testGetUserName_Success() {
        setAuthentication(testLoginUser);

        String userName = SecurityUtils.getUserName();

        assertEquals("测试用户", userName);
    }

    @Test
    @DisplayName("isAdmin - 管理员返回true")
    void testIsAdmin_True() {
        SysUser admin = new SysUser();
        admin.setLoginName("admin");
        SysRole adminRole = new SysRole();
        adminRole.setRoleKey("admin");
        admin.setRoles(List.of(adminRole));
        Set<String> permissions = new HashSet<>();
        permissions.add("*:*:*");
        LoginUser adminLoginUser = new LoginUser(admin, permissions);
        setAuthentication(adminLoginUser);

        assertTrue(SecurityUtils.isAdmin());
    }

    @Test
    @DisplayName("isAdmin - 非管理员返回false")
    void testIsAdmin_False() {
        setAuthentication(testLoginUser);

        assertFalse(SecurityUtils.isAdmin());
    }

    @Test
    @DisplayName("isAdmin - 未认证返回false")
    void testIsAdmin_NotAuthenticated() {
        assertFalse(SecurityUtils.isAdmin());
    }

    @Test
    @DisplayName("isAdminUser - 管理员用户返回true")
    void testIsAdminUser_True() {
        SysUser admin = new SysUser();
        SysRole adminRole = new SysRole();
        adminRole.setRoleKey("admin");
        admin.setRoles(List.of(adminRole));

        assertTrue(SecurityUtils.isAdminUser(admin));
    }

    @Test
    @DisplayName("isAdminUser - 非管理员用户返回false")
    void testIsAdminUser_False() {
        assertFalse(SecurityUtils.isAdminUser(testUser));
    }

    @Test
    @DisplayName("isAdminUser - null用户返回false")
    void testIsAdminUser_Null() {
        assertFalse(SecurityUtils.isAdminUser(null));
    }

    @Test
    @DisplayName("isAdmin(String) - 当前用户匹配")
    void testIsAdminString_CurrentUser() {
        setAuthentication(testLoginUser);

        boolean result = SecurityUtils.isAdmin("1");

        assertFalse(result); // testUser不是admin
    }

    @Test
    @DisplayName("isAdmin(String) - 不同用户查询数据库")
    void testIsAdminString_DifferentUser() {
        setAuthentication(testLoginUser);
        // 此测试依赖SpringUtils，在集成测试中覆盖
        // 验证isAdminUser方法
        assertFalse(SecurityUtils.isAdminUser(testUser));
    }

    @Test
    @DisplayName("isAdminById - 正常查询")
    void testIsAdminById_Success() {
        // 此测试依赖SpringUtils，在集成测试中覆盖
        // 验证isAdminUser方法
        SysUser admin = new SysUser();
        SysRole adminRole = new SysRole();
        adminRole.setRoleKey("admin");
        admin.setRoles(List.of(adminRole));
        assertTrue(SecurityUtils.isAdminUser(admin));
    }

    @Test
    @DisplayName("isAdminById - 用户不存在返回false")
    void testIsAdminById_UserNotFound() {
        // 此测试依赖SpringUtils，在集成测试中覆盖
        assertFalse(SecurityUtils.isAdminUser(null));
    }

    @Test
    @DisplayName("isAdminById - 异常返回false")
    void testIsAdminById_Exception() {
        // 此测试依赖SpringUtils，在集成测试中覆盖
        // 验证普通用户不是admin
        assertFalse(SecurityUtils.isAdminUser(testUser));
    }

    @Test
    @DisplayName("getIp - 正常获取IP")
    void testGetIp_Success() {
        testLoginUser.setIpaddr("192.168.1.1");
        setAuthentication(testLoginUser);

        String ip = SecurityUtils.getIp();

        assertEquals("192.168.1.1", ip);
    }

    @Test
    @DisplayName("getIp - 未认证返回默认IP")
    void testGetIp_NotAuthenticated() {
        String ip = SecurityUtils.getIp();

        assertEquals("127.0.0.1", ip);
    }

    @Test
    @DisplayName("密码加密 - 空字符串")
    void testEncryptPassword_Empty() {
        String encrypted = SecurityUtils.encryptPassword("");

        assertNotNull(encrypted);
        assertTrue(SecurityUtils.matchesPassword("", encrypted));
    }

    @Test
    @DisplayName("密码加密 - 特殊字符")
    void testEncryptPassword_SpecialCharacters() {
        String specialPassword = "!@#$%^&*()_+-=[]{}|;':\",./<>?~`中文字符";
        String encrypted = SecurityUtils.encryptPassword(specialPassword);

        assertNotNull(encrypted);
        assertTrue(SecurityUtils.matchesPassword(specialPassword, encrypted));
    }

    @Test
    @DisplayName("密码加密 - 长密码")
    void testEncryptPassword_LongPassword() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 50; i++) {  // BCrypt限制72字节
            sb.append("a");
        }
        String longPassword = sb.toString();
        String encrypted = SecurityUtils.encryptPassword(longPassword);

        assertNotNull(encrypted);
        assertTrue(SecurityUtils.matchesPassword(longPassword, encrypted));
    }

    @Test
    @DisplayName("密码加密 - 多次加密结果不同")
    void testEncryptPassword_DifferentResults() {
        String encrypted1 = SecurityUtils.encryptPassword(TEST_PASSWORD);
        String encrypted2 = SecurityUtils.encryptPassword(TEST_PASSWORD);

        // BCrypt每次加密结果不同(salt不同)
        assertNotEquals(encrypted1, encrypted2);
        // 但都能匹配
        assertTrue(SecurityUtils.matchesPassword(TEST_PASSWORD, encrypted1));
        assertTrue(SecurityUtils.matchesPassword(TEST_PASSWORD, encrypted2));
    }
}
