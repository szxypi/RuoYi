package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.FdtempApplication;
import com.zjjh.fdtemp.TestConfig;
import com.zjjh.fdtemp.beans.LoginUser;
import com.zjjh.fdtemp.beans.entity.SysRole;
import com.zjjh.fdtemp.beans.entity.SysUser;
import com.zjjh.fdtemp.beans.entity.SysUserRole;
import com.zjjh.fdtemp.common.exception.ServiceException;
import com.zjjh.fdtemp.constants.UserConstants;
import com.zjjh.fdtemp.service.SysRoleService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = FdtempApplication.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class SysRoleServiceImplTest {

    @Autowired
    private SysRoleService roleService;

    private static final String ADMIN_ROLE_ID = "1";
    private static final String COMMON_ROLE_ID = "2";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        SysUser adminUser = new SysUser();
        adminUser.setId("1");
        adminUser.setLoginName("admin");
        adminUser.setUserName("超级管理员");
        adminUser.setStatus("0");

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
    @DisplayName("1.1 查询所有角色列表")
    void testSelectRoleList() {
        SysRole query = new SysRole();
        List<SysRole> list = roleService.selectRoleList(query);
        assertNotNull(list);
        assertTrue(list.size() >= 3);
    }

    @Test
    @Order(2)
    @DisplayName("1.2 根据ID查询角色")
    void testSelectRoleById() {
        SysRole role = roleService.selectRoleById(ADMIN_ROLE_ID);
        assertNotNull(role);
        assertEquals("超级管理员", role.getRoleName());
        assertEquals("admin", role.getRoleKey());
    }

    @Test
    @Order(3)
    @DisplayName("1.3 查询所有角色")
    void testSelectRoleAll() {
        List<SysRole> list = roleService.selectRoleAll();
        assertNotNull(list);
        assertTrue(list.size() >= 3);
    }

    @Test
    @Order(4)
    @DisplayName("1.4 根据用户ID查询角色")
    void testSelectRolesByUserId() {
        List<SysRole> roles = roleService.selectRolesByUserId("1");
        assertNotNull(roles);
    }

    @Test
    @Order(5)
    @DisplayName("1.5 根据用户ID查询角色权限键")
    void testSelectRoleKeys() {
        Set<String> roleKeys = roleService.selectRoleKeys("1");
        assertNotNull(roleKeys);
        assertTrue(roleKeys.contains("admin"));
    }

    @Test
    @Order(6)
    @DisplayName("1.6 查询角色使用数量")
    void testCountUserRoleByRoleId() {
        int count = roleService.countUserRoleByRoleId(ADMIN_ROLE_ID);
        assertTrue(count >= 1);
    }

    @Test
    @Order(10)
    @DisplayName("2.1 校验角色名称唯一性 - 已存在")
    void testCheckRoleNameUnique_Existing() {
        SysRole role = new SysRole();
        role.setRoleName("超级管理员");
        boolean result = roleService.checkRoleNameUnique(role);
        assertEquals(UserConstants.NOT_UNIQUE, result);
    }

    @Test
    @Order(11)
    @DisplayName("2.2 校验角色名称唯一性 - 不存在")
    void testCheckRoleNameUnique_New() {
        SysRole role = new SysRole();
        role.setRoleName("新角色名称测试");
        boolean result = roleService.checkRoleNameUnique(role);
        assertEquals(UserConstants.UNIQUE, result);
    }

    @Test
    @Order(12)
    @DisplayName("2.3 校验角色权限键唯一性 - 已存在")
    void testCheckRoleKeyUnique_Existing() {
        SysRole role = new SysRole();
        role.setRoleKey("admin");
        boolean result = roleService.checkRoleKeyUnique(role);
        assertEquals(UserConstants.NOT_UNIQUE, result);
    }

    @Test
    @Order(13)
    @DisplayName("2.4 校验角色权限键唯一性 - 不存在")
    void testCheckRoleKeyUnique_New() {
        SysRole role = new SysRole();
        role.setRoleKey("new_role_key_test");
        boolean result = roleService.checkRoleKeyUnique(role);
        assertEquals(UserConstants.UNIQUE, result);
    }

    @Test
    @Order(20)
    @DisplayName("3.1 新增角色成功")
    @Disabled("H2 不支持 MySQL 的 sysdate() 函数")
    void testInsertRole() {
        SysRole role = createTestRole("测试角色", "test_role_insert");
        role.setMenuIds(new String[]{"1", "2"});
        int result = roleService.insertRole(role);
        assertTrue(result >= 0);
    }

    @Test
    @Order(30)
    @DisplayName("4.1 修改角色成功")
    @Disabled("H2 不支持 MySQL 的 sysdate() 函数")
    void testUpdateRole() {
        SysRole role = roleService.selectRoleById(COMMON_ROLE_ID);
        assertNotNull(role);
        String newRoleName = "修改后的普通角色";
        role.setRoleName(newRoleName);
        role.setMenuIds(new String[]{"1", "2", "3"});
        int result = roleService.updateRole(role);
        assertTrue(result >= 0);
    }

    @Test
    @Order(50)
    @DisplayName("6.1 校验管理员角色是否允许操作 - 不允许")
    void testCheckRoleAllowed_Admin() {
        SysRole adminRole = new SysRole();
        adminRole.setId(ADMIN_ROLE_ID);
        adminRole.setRoleKey("admin");
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            roleService.checkRoleAllowed(adminRole);
        });
        assertTrue(exception.getMessage().contains("超级管理员角色"));
    }

    @Test
    @Order(60)
    @DisplayName("7.1 取消授权用户角色")
    void testDeleteAuthUser() {
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId("2");
        userRole.setRoleId(COMMON_ROLE_ID);
        int result = roleService.deleteAuthUser(userRole);
        assertTrue(result >= 0);
    }

    @Test
    @Order(61)
    @DisplayName("7.2 批量选择授权用户角色")
    @Disabled("H2 不支持 MySQL 的 sysdate() 函数")
    void testInsertAuthUsers() {
        int result = roleService.insertAuthUsers(COMMON_ROLE_ID, "2");
        assertTrue(result >= 0);
    }

    private SysRole createTestRole(String roleName, String roleKey) {
        SysRole role = new SysRole();
        role.setRoleName(roleName);
        role.setRoleKey(roleKey);
        role.setRoleSort("99");
        role.setDataScope("1");
        role.setStatus("0");
        return role;
    }
}
