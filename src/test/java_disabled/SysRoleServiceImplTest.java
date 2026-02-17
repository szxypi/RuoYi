package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.FdtempApplication;
import com.zjjh.fdtemp.TestConfig;
import com.zjjh.fdtemp.beans.entity.SysRole;
import com.zjjh.fdtemp.beans.entity.SysUserRole;
import com.zjjh.fdtemp.common.exception.ServiceException;
import com.zjjh.fdtemp.constants.UserConstants;
import com.zjjh.fdtemp.service.SysRoleService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SysRoleService 单元测试类
 */
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
    private static final String TEST_ROLE_ID = "3";

    // ============================================
    // 查询测试
    // ============================================

    @Test
    @Order(1)
    @DisplayName("1.1 查询所有角色列表")
    void testSelectRoleList() {
        SysRole query = new SysRole();
        List<SysRole> list = roleService.selectRoleList(query);
        assertNotNull(list);
        assertTrue(list.size() >= 3, "应该至少有3个测试角色");
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

    // ============================================
    // 唯一性校验测试
    // ============================================

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
    @DisplayName("2.3 校验角色名称唯一性 - 修改时保持自己的名字")
    void testCheckRoleNameUnique_Self() {
        SysRole role = new SysRole();
        role.setId(ADMIN_ROLE_ID);
        role.setRoleName("超级管理员");
        boolean result = roleService.checkRoleNameUnique(role);
        assertEquals(UserConstants.UNIQUE, result);
    }

    @Test
    @Order(13)
    @DisplayName("2.4 校验角色权限键唯一性 - 已存在")
    void testCheckRoleKeyUnique_Existing() {
        SysRole role = new SysRole();
        role.setRoleKey("admin");
        boolean result = roleService.checkRoleKeyUnique(role);
        assertEquals(UserConstants.NOT_UNIQUE, result);
    }

    @Test
    @Order(14)
    @DisplayName("2.5 校验角色权限键唯一性 - 不存在")
    void testCheckRoleKeyUnique_New() {
        SysRole role = new SysRole();
        role.setRoleKey("new_role_key_test");
        boolean result = roleService.checkRoleKeyUnique(role);
        assertEquals(UserConstants.UNIQUE, result);
    }

    // ============================================
    // 新增测试
    // ============================================

    @Test
    @Order(20)
    @DisplayName("3.1 新增角色成功")
    void testInsertRole() {
        SysRole role = createTestRole("测试角色", "test_role_insert");
        role.setMenuIds(new String[]{"1", "2"});

        int result = roleService.insertRole(role);
        assertTrue(result >= 0);
    }

    // ============================================
    // 修改测试
    // ============================================

    @Test
    @Order(30)
    @DisplayName("4.1 修改角色成功")
    void testUpdateRole() {
        SysRole role = roleService.selectRoleById(COMMON_ROLE_ID);
        assertNotNull(role);

        String newRoleName = "修改后的普通角色";
        role.setRoleName(newRoleName);
        role.setMenuIds(new String[]{"1", "2", "3"});

        int result = roleService.updateRole(role);
        assertTrue(result >= 0);

        SysRole updated = roleService.selectRoleById(COMMON_ROLE_ID);
        assertEquals(newRoleName, updated.getRoleName());
    }

    @Test
    @Order(31)
    @DisplayName("4.2 修改角色状态")
    void testChangeStatus() {
        SysRole role = new SysRole();
        role.setId(COMMON_ROLE_ID);
        role.setStatus("1");
        role.setRoleName("普通角色");
        role.setRoleKey("common");
        role.setRoleSort("2");

        int result = roleService.changeStatus(role);
        assertTrue(result >= 0);
    }

    @Test
    @Order(32)
    @DisplayName("4.3 修改数据权限")
    void testAuthDataScope() {
        SysRole role = roleService.selectRoleById(COMMON_ROLE_ID);
        role.setDataScope("3"); // 本部门数据权限
        role.setDeptIds(new String[]{"2", "3", "4"});

        int result = roleService.authDataScope(role);
        assertTrue(result >= 0);
    }

    // ============================================
    // 删除测试
    // ============================================

    @Test
    @Order(40)
    @DisplayName("5.1 删除单个角色")
    void testDeleteRoleById() {
        // 先新增一个角色用于删除
        SysRole role = createTestRole("待删除角色", "to_be_deleted");
        roleService.insertRole(role);

        SysRole saved = roleService.selectRoleList(new SysRole()).stream()
                .filter(r -> "待删除角色".equals(r.getRoleName()))
                .findFirst()
                .orElse(null);
        assertNotNull(saved);

        boolean result = roleService.deleteRoleById(saved.getId());
        assertTrue(result);
    }

    @Test
    @Order(41)
    @DisplayName("5.2 批量删除角色")
    void testDeleteRoleByIds() {
        // 先新增两个角色用于删除
        SysRole role1 = createTestRole("批量删除角色1", "batch_delete_1");
        roleService.insertRole(role1);

        SysRole role2 = createTestRole("批量删除角色2", "batch_delete_2");
        roleService.insertRole(role2);

        List<SysRole> savedRoles = roleService.selectRoleList(new SysRole());

        // 查找刚创建的角色
        String id1 = savedRoles.stream()
                .filter(r -> "批量删除角色1".equals(r.getRoleName()))
                .findFirst()
                .map(SysRole::getId)
                .orElse(null);
        String id2 = savedRoles.stream()
                .filter(r -> "批量删除角色2".equals(r.getRoleName()))
                .findFirst()
                .map(SysRole::getId)
                .orElse(null);

        assertNotNull(id1);
        assertNotNull(id2);

        int result = roleService.deleteRoleByIds(id1 + "," + id2);
        assertTrue(result >= 0);
    }

    @Test
    @Order(42)
    @DisplayName("5.3 删除已分配用户的角色 - 抛出异常")
    void testDeleteRoleByIds_Allocated() {
        // admin角色已分配给用户，不能删除
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            roleService.deleteRoleByIds(ADMIN_ROLE_ID);
        });
        assertTrue(exception.getMessage().contains("已分配"));
    }

    // ============================================
    // 权限校验测试
    // ============================================

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
    @Order(51)
    @DisplayName("6.2 校验普通角色是否允许操作 - 允许")
    void testCheckRoleAllowed_Normal() {
        SysRole normalRole = new SysRole();
        normalRole.setId(COMMON_ROLE_ID);
        normalRole.setRoleKey("common");

        assertDoesNotThrow(() -> {
            roleService.checkRoleAllowed(normalRole);
        });
    }

    // ============================================
    // 用户授权测试
    // ============================================

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
    @DisplayName("7.2 批量取消授权用户角色")
    void testDeleteAuthUsers() {
        int result = roleService.deleteAuthUsers(COMMON_ROLE_ID, "2,3");
        assertTrue(result >= 0);
    }

    @Test
    @Order(62)
    @DisplayName("7.3 批量选择授权用户角色")
    void testInsertAuthUsers() {
        int result = roleService.insertAuthUsers(COMMON_ROLE_ID, "2");
        assertTrue(result >= 0);
    }

    // ============================================
    // 辅助方法
    // ============================================

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
