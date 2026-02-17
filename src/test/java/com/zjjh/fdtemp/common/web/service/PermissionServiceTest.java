package com.zjjh.fdtemp.common.web.service;

import com.zjjh.fdtemp.beans.LoginUser;
import com.zjjh.fdtemp.beans.entity.SysUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PermissionService 服务层单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("权限服务测试")
class PermissionServiceTest {

    @InjectMocks
    private PermissionService permissionService;

    private LoginUser loginUser;
    private SysUser user;

    @BeforeEach
    void setUp() {
        // 清理安全上下文
        SecurityContextHolder.clearContext();

        user = new SysUser();
        user.setId("1");
        user.setLoginName("admin");
        user.setUserName("管理员");
        user.setPassword("password");

        Set<String> permissions = new HashSet<>();
        permissions.add("system:user:list");
        permissions.add("system:user:add");
        permissions.add("system:role:list");

        loginUser = new LoginUser(user, permissions);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    /**
     * 使用真实的 UsernamePasswordAuthenticationToken 设置安全上下文
     */
    private void setupSecurityContext(LoginUser user) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /**
     * 使用自定义权限设置安全上下文（用于角色测试）
     */
    private void setupSecurityContextWithAuthorities(Set<String> authorities) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                loginUser, null,
                authorities.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /**
     * 使用角色设置安全上下文
     */
    private void setupSecurityContextWithRoles(Set<String> roles) {
        setupSecurityContextWithAuthorities(roles);
    }

    private void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("测试hasPermi - 有权限")
    void testHasPermiWithPermission() {
        setupSecurityContext(loginUser);

        String result = permissionService.hasPermi("system:user:list");

        assertEquals("", result); // 空字符串表示有权限
    }

    @Test
    @DisplayName("测试hasPermi - 无权限")
    void testHasPermiWithoutPermission() {
        setupSecurityContext(loginUser);

        String result = permissionService.hasPermi("system:dept:list");

        assertEquals("hidden", result); // hidden表示无权限，前端隐藏
    }

    @Test
    @DisplayName("测试hasPermi - 未登录")
    void testHasPermiNotAuthenticated() {
        clearSecurityContext();

        String result = permissionService.hasPermi("system:user:list");

        assertEquals("hidden", result);
    }

    @Test
    @DisplayName("测试lacksPermi - 缺少权限")
    void testLacksPermiWithoutPermission() {
        setupSecurityContext(loginUser);

        String result = permissionService.lacksPermi("system:dept:list");

        assertEquals("", result); // 空字符串表示缺少权限（符合预期）
    }

    @Test
    @DisplayName("测试lacksPermi - 有权限")
    void testLacksPermiWithPermission() {
        setupSecurityContext(loginUser);

        String result = permissionService.lacksPermi("system:user:list");

        assertEquals("hidden", result);
    }

    @Test
    @DisplayName("测试hasAnyPermi - 有任一权限")
    void testHasAnyPermiWithPermission() {
        setupSecurityContext(loginUser);

        String result = permissionService.hasAnyPermi("system:user:list,system:dept:list");

        assertEquals("", result);
    }

    @Test
    @DisplayName("测试hasAnyPermi - 无任一权限")
    void testHasAnyPermiWithoutPermission() {
        setupSecurityContext(loginUser);

        String result = permissionService.hasAnyPermi("system:dept:list,system:menu:list");

        assertEquals("hidden", result);
    }

    @Test
    @DisplayName("测试hasRole - 有角色")
    void testHasRoleWithRole() {
        Set<String> permissions = new HashSet<>();
        permissions.add("ROLE_admin");
        setupSecurityContextWithRoles(permissions);

        String result = permissionService.hasRole("admin");

        assertEquals("", result);
    }

    @Test
    @DisplayName("测试hasRole - 无角色")
    void testHasRoleWithoutRole() {
        Set<String> permissions = new HashSet<>();
        permissions.add("ROLE_user");
        setupSecurityContextWithRoles(permissions);

        String result = permissionService.hasRole("admin");

        assertEquals("hidden", result);
    }

    @Test
    @DisplayName("测试lacksRole - 缺少角色")
    void testLacksRoleWithoutRole() {
        Set<String> permissions = new HashSet<>();
        permissions.add("ROLE_user");
        setupSecurityContextWithRoles(permissions);

        String result = permissionService.lacksRole("admin");

        assertEquals("", result);
    }

    @Test
    @DisplayName("测试hasAnyRoles - 有任一角色")
    void testHasAnyRolesWithRole() {
        Set<String> permissions = new HashSet<>();
        permissions.add("ROLE_admin");
        setupSecurityContextWithRoles(permissions);

        String result = permissionService.hasAnyRoles("admin,user,guest");

        assertEquals("", result);
    }

    @Test
    @DisplayName("测试hasAnyRoles - 无任一角色")
    void testHasAnyRolesWithoutRole() {
        Set<String> permissions = new HashSet<>();
        permissions.add("ROLE_guest");
        setupSecurityContextWithRoles(permissions);

        String result = permissionService.hasAnyRoles("admin,user");

        assertEquals("hidden", result);
    }

    @Test
    @DisplayName("测试isUser - 已认证用户")
    void testIsUserAuthenticated() {
        setupSecurityContext(loginUser);

        boolean result = permissionService.isUser();

        assertTrue(result);
    }

    @Test
    @DisplayName("测试isUser - 未认证")
    void testIsUserNotAuthenticated() {
        clearSecurityContext();

        boolean result = permissionService.isUser();

        assertFalse(result);
    }

    @Test
    @DisplayName("测试isPermitted - 有权限")
    void testIsPermittedWithPermission() {
        setupSecurityContext(loginUser);

        boolean result = permissionService.isPermitted("system:user:list");

        assertTrue(result);
    }

    @Test
    @DisplayName("测试isPermitted - 无权限")
    void testIsPermittedWithoutPermission() {
        setupSecurityContext(loginUser);

        boolean result = permissionService.isPermitted("system:dept:list");

        assertFalse(result);
    }

    @Test
    @DisplayName("测试isLacksPermitted - 缺少权限")
    void testIsLacksPermitted() {
        setupSecurityContext(loginUser);

        boolean result = permissionService.isLacksPermitted("system:dept:list");

        assertTrue(result);
    }

    @Test
    @DisplayName("测试hasAnyPermissions - 有任一权限")
    void testHasAnyPermissions() {
        setupSecurityContext(loginUser);

        boolean result = permissionService.hasAnyPermissions("system:user:list,system:dept:list");

        assertTrue(result);
    }

    @Test
    @DisplayName("测试hasAnyPermissions - 无任一权限")
    void testHasAnyPermissionsNone() {
        setupSecurityContext(loginUser);

        boolean result = permissionService.hasAnyPermissions("system:dept:list,system:menu:list");

        assertFalse(result);
    }

    @Test
    @DisplayName("测试hasAnyPermissions - 自定义分隔符")
    void testHasAnyPermissionsCustomDelimiter() {
        setupSecurityContext(loginUser);

        boolean result = permissionService.hasAnyPermissions("system:user:list;system:dept:list", ";");

        assertTrue(result);
    }

    @Test
    @DisplayName("测试isRole - 有角色")
    void testIsRoleWithRole() {
        Set<String> permissions = new HashSet<>();
        permissions.add("ROLE_admin");
        setupSecurityContextWithRoles(permissions);

        boolean result = permissionService.isRole("admin");

        assertTrue(result);
    }

    @Test
    @DisplayName("测试isRole - 带ROLE_前缀匹配")
    void testIsRoleWithRolePrefix() {
        Set<String> permissions = new HashSet<>();
        permissions.add("admin");
        setupSecurityContextWithRoles(permissions);

        boolean result = permissionService.isRole("admin");

        assertTrue(result);
    }

    @Test
    @DisplayName("测试isLacksRole - 缺少角色")
    void testIsLacksRole() {
        Set<String> permissions = new HashSet<>();
        permissions.add("ROLE_user");
        setupSecurityContextWithRoles(permissions);

        boolean result = permissionService.isLacksRole("admin");

        assertTrue(result);
    }

    @Test
    @DisplayName("测试isAnyRoles - 有任一角色")
    void testIsAnyRoles() {
        Set<String> permissions = new HashSet<>();
        permissions.add("ROLE_admin");
        setupSecurityContextWithRoles(permissions);

        boolean result = permissionService.isAnyRoles("admin,user");

        assertTrue(result);
    }

    @Test
    @DisplayName("测试isAnyRoles - 自定义分隔符")
    void testIsAnyRolesCustomDelimiter() {
        Set<String> permissions = new HashSet<>();
        permissions.add("ROLE_admin");
        setupSecurityContextWithRoles(permissions);

        boolean result = permissionService.isAnyRoles("admin;user", ";");

        assertTrue(result);
    }

    /**
     * 注意：getPrincipalProperty 使用 Java Bean Introspector 来反射获取属性，
     * 这会触发 SysUser 关联类（如 SysDept）的类加载。
     * 由于单元测试环境不包含完整的类依赖，这些测试应在集成测试中进行。
     * 此处测试未认证情况，不涉及类加载问题。
     */
    @Test
    @DisplayName("测试getPrincipalProperty - 未认证")
    void testGetPrincipalPropertyNotAuthenticated() {
        clearSecurityContext();

        Object result = permissionService.getPrincipalProperty("loginName");

        assertNull(result);
    }

    @Test
    @DisplayName("测试NOACCESS常量")
    void testNoAccessConstant() {
        assertEquals("hidden", PermissionService.NOACCESS);
    }

    @Test
    @DisplayName("测试空分隔符使用默认值")
    void testEmptyDelimiterUsesDefault() {
        setupSecurityContext(loginUser);

        boolean result = permissionService.hasAnyPermissions("system:user:list", "");

        assertTrue(result);
    }

    @Test
    @DisplayName("测试null分隔符使用默认值")
    void testNullDelimiterUsesDefault() {
        setupSecurityContext(loginUser);

        boolean result = permissionService.hasAnyPermissions("system:user:list", null);

        assertTrue(result);
    }

    @Test
    @DisplayName("测试权限字符串前后空格处理")
    void testPermissionTrim() {
        setupSecurityContext(loginUser);

        boolean result = permissionService.hasAnyPermissions(" system:user:list , system:dept:list ");

        assertTrue(result);
    }

    @Test
    @DisplayName("测试角色字符串前后空格处理")
    void testRoleTrim() {
        Set<String> permissions = new HashSet<>();
        permissions.add("ROLE_admin");
        setupSecurityContextWithRoles(permissions);

        boolean result = permissionService.isAnyRoles(" admin , user ");

        assertTrue(result);
    }
}
