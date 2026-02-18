package com.zjjh.fdtemp.config.security;

import com.zjjh.fdtemp.beans.LoginUser;
import com.zjjh.fdtemp.beans.entity.SysRole;
import com.zjjh.fdtemp.beans.entity.SysUser;
import com.zjjh.fdtemp.common.exception.ServiceException;
import com.zjjh.fdtemp.enums.UserStatus;
import com.zjjh.fdtemp.service.SysMenuService;
import com.zjjh.fdtemp.service.SysUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * UserDetailsServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl 测试")
class UserDetailsServiceImplTest {

    @Mock
    private SysUserService userService;

    @Mock
    private SysMenuService menuService;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private SysUser normalUser;
    private SysUser adminUser;
    private SysUser deletedUser;
    private SysUser disabledUser;
    private Set<String> testPermissions;

    @BeforeEach
    void setUp() {
        // 创建正常用户
        normalUser = new SysUser();
        normalUser.setId("2");
        normalUser.setLoginName("normaluser");
        normalUser.setUserName("普通用户");
        normalUser.setPassword("$2a$10$encodedPassword");
        normalUser.setStatus("0");
        normalUser.setYn("0");

        // 创建管理员用户
        adminUser = new SysUser();
        adminUser.setId("1");
        adminUser.setLoginName("admin");
        adminUser.setUserName("管理员");
        adminUser.setPassword("$2a$10$encodedPassword");
        adminUser.setStatus("0");
        adminUser.setYn("0");
        SysRole adminRole = new SysRole();
        adminRole.setRoleKey("admin");
        adminUser.setRoles(List.of(adminRole));

        // 创建已删除用户
        deletedUser = new SysUser();
        deletedUser.setId("3");
        deletedUser.setLoginName("deleteduser");
        deletedUser.setUserName("已删除用户");
        deletedUser.setPassword("$2a$10$encodedPassword");
        deletedUser.setStatus("0");
        deletedUser.setYn(UserStatus.DELETED.getCode());

        // 创建已停用用户
        disabledUser = new SysUser();
        disabledUser.setId("4");
        disabledUser.setLoginName("disableduser");
        disabledUser.setUserName("已停用用户");
        disabledUser.setPassword("$2a$10$encodedPassword");
        disabledUser.setStatus(UserStatus.DISABLE.getCode());
        disabledUser.setYn("0");

        // 测试权限
        testPermissions = new HashSet<>();
        testPermissions.add("system:user:list");
        testPermissions.add("system:user:query");
    }

    @Test
    @DisplayName("loadUserByUsername - 正常用户加载成功")
    void testLoadUserByUsername_NormalUser() {
        when(userService.selectUserByLoginName("normaluser")).thenReturn(normalUser);
        when(menuService.selectPermsByUserId("2")).thenReturn(testPermissions);

        UserDetails userDetails = userDetailsService.loadUserByUsername("normaluser");

        assertNotNull(userDetails);
        assertInstanceOf(LoginUser.class, userDetails);
        LoginUser loginUser = (LoginUser) userDetails;
        assertEquals("normaluser", loginUser.getUsername());
        assertEquals("2", loginUser.getUserId());
        assertEquals(testPermissions, loginUser.getPermissions());
        verify(menuService).selectPermsByUserId("2");
        verify(menuService, never()).selectPermsAll();
    }

    @Test
    @DisplayName("loadUserByUsername - 管理员加载所有权限")
    void testLoadUserByUsername_AdminUser() {
        Set<String> allPermissions = new HashSet<>();
        allPermissions.add("*:*:*");
        when(userService.selectUserByLoginName("admin")).thenReturn(adminUser);
        when(menuService.selectPermsAll()).thenReturn(allPermissions);

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        assertNotNull(userDetails);
        LoginUser loginUser = (LoginUser) userDetails;
        assertEquals("admin", loginUser.getUsername());
        assertEquals(allPermissions, loginUser.getPermissions());
        verify(menuService).selectPermsAll();
        verify(menuService, never()).selectPermsByUserId(anyString());
    }

    @Test
    @DisplayName("loadUserByUsername - 用户不存在抛出异常")
    void testLoadUserByUsername_UserNotFound() {
        when(userService.selectUserByLoginName("nonexistent")).thenReturn(null);

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonexistent")
        );

        assertTrue(exception.getMessage().contains("不存在"));
        verify(userService).selectUserByLoginName("nonexistent");
    }

    @Test
    @DisplayName("loadUserByUsername - 已删除用户抛出异常")
    void testLoadUserByUsername_DeletedUser() {
        when(userService.selectUserByLoginName("deleteduser")).thenReturn(deletedUser);

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> userDetailsService.loadUserByUsername("deleteduser")
        );

        assertTrue(exception.getMessage().contains("已被删除"));
    }

    @Test
    @DisplayName("loadUserByUsername - 已停用用户抛出异常")
    void testLoadUserByUsername_DisabledUser() {
        when(userService.selectUserByLoginName("disableduser")).thenReturn(disabledUser);

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> userDetailsService.loadUserByUsername("disableduser")
        );

        assertTrue(exception.getMessage().contains("已停用"));
    }

    @Test
    @DisplayName("loadUserByUsername - 空用户名")
    void testLoadUserByUsername_EmptyUsername() {
        when(userService.selectUserByLoginName("")).thenReturn(null);

        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("")
        );
    }

    @Test
    @DisplayName("loadUserByUsername - null用户名")
    void testLoadUserByUsername_NullUsername() {
        when(userService.selectUserByLoginName(null)).thenReturn(null);

        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(null)
        );
    }

    @Test
    @DisplayName("loadUserByUsername - 权限列表为空")
    void testLoadUserByUsername_EmptyPermissions() {
        when(userService.selectUserByLoginName("normaluser")).thenReturn(normalUser);
        when(menuService.selectPermsByUserId("2")).thenReturn(new HashSet<>());

        UserDetails userDetails = userDetailsService.loadUserByUsername("normaluser");

        assertNotNull(userDetails);
        LoginUser loginUser = (LoginUser) userDetails;
        assertTrue(loginUser.getPermissions().isEmpty());
    }

    @Test
    @DisplayName("loadUserByUsername - 验证UserDetails方法")
    void testLoadUserByUsername_UserDetailsMethods() {
        when(userService.selectUserByLoginName("normaluser")).thenReturn(normalUser);
        when(menuService.selectPermsByUserId("2")).thenReturn(testPermissions);

        UserDetails userDetails = userDetailsService.loadUserByUsername("normaluser");

        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
        assertEquals("$2a$10$encodedPassword", userDetails.getPassword());
        assertEquals("normaluser", userDetails.getUsername());
        assertNotNull(userDetails.getAuthorities());
        assertEquals(2, userDetails.getAuthorities().size());
    }

    @Test
    @DisplayName("loadUserByUsername - 大小写敏感测试")
    void testLoadUserByUsername_CaseSensitive() {
        when(userService.selectUserByLoginName("NormalUser")).thenReturn(null);

        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("NormalUser")
        );

        verify(userService).selectUserByLoginName("NormalUser");
        verify(userService, never()).selectUserByLoginName("normaluser");
    }

    @Test
    @DisplayName("loadUserByUsername - 包含特殊字符的用户名")
    void testLoadUserByUsername_SpecialCharacters() {
        String specialUsername = "user@#$%^&*";
        when(userService.selectUserByLoginName(specialUsername)).thenReturn(null);

        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(specialUsername)
        );
    }

    @Test
    @DisplayName("loadUserByUsername - 长用户名")
    void testLoadUserByUsername_LongUsername() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("a");
        }
        String longUsername = sb.toString();
        when(userService.selectUserByLoginName(longUsername)).thenReturn(null);

        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(longUsername)
        );
    }
}
