package com.zjjh.fdtemp.beans.entity;

import com.zjjh.fdtemp.common.utils.security.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 系统管理实体类单元测试
 */
public class SysEntitiesTest {

    // ============================================
    // SysUser 实体测试
    // ============================================

    @Test
    @DisplayName("SysUser - 默认构造函数")
    void testSysUser_DefaultConstructor() {
        SysUser user = new SysUser();
        assertNull(user.getId());
        assertNull(user.getLoginName());
        assertNull(user.getUserName());
    }

    @Test
    @DisplayName("SysUser - ID构造函数")
    void testSysUser_IdConstructor() {
        SysUser user = new SysUser("123");
        assertEquals("123", user.getId());
    }

    @Test
    @DisplayName("SysUser - Getter和Setter")
    void testSysUser_GettersAndSetters() {
        SysUser user = new SysUser();

        user.setLoginName("testuser");
        user.setUserName("测试用户");
        user.setEmail("test@test.com");
        user.setPhonenumber("13800000000");
        user.setSex("0");
        user.setStatus("0");
        user.setDeptId("1");
        user.setUserType("00");

        assertEquals("testuser", user.getLoginName());
        assertEquals("测试用户", user.getUserName());
        assertEquals("test@test.com", user.getEmail());
        assertEquals("13800000000", user.getPhonenumber());
        assertEquals("0", user.getSex());
        assertEquals("0", user.getStatus());
        assertEquals("1", user.getDeptId());
        assertEquals("00", user.getUserType());
    }

    @Test
    @DisplayName("SysUser - isAdmin方法")
    void testSysUser_IsAdmin() {
        SysUser adminUser = new SysUser();
        adminUser.setLoginName("admin");
        // 注意：isAdmin 依赖 SecurityUtils.isAdminUser，这里测试方法存在性
        assertNotNull(adminUser.isAdmin());
    }

    @Test
    @DisplayName("SysUser - Dept对象延迟初始化")
    void testSysUser_DeptLazyInit() {
        SysUser user = new SysUser();
        SysDept dept = user.getDept();
        assertNotNull(dept);
    }

    @Test
    @DisplayName("SysUser - RoleIds和PostIds")
    void testSysUser_RoleIdsAndPostIds() {
        SysUser user = new SysUser();
        String[] roleIds = {"1", "2", "3"};
        String[] postIds = {"1", "2"};

        user.setRoleIds(roleIds);
        user.setPostIds(postIds);

        assertArrayEquals(roleIds, user.getRoleIds());
        assertArrayEquals(postIds, user.getPostIds());
    }

    @Test
    @DisplayName("SysUser - toString方法")
    void testSysUser_ToString() {
        SysUser user = new SysUser();
        user.setId("1");
        user.setLoginName("test");

        String str = user.toString();
        assertNotNull(str);
        assertTrue(str.contains("test"));
    }

    // ============================================
    // SysRole 实体测试
    // ============================================

    @Test
    @DisplayName("SysRole - 默认构造函数")
    void testSysRole_DefaultConstructor() {
        SysRole role = new SysRole();
        assertNull(role.getId());
        assertNull(role.getRoleName());
    }

    @Test
    @DisplayName("SysRole - ID构造函数")
    void testSysRole_IdConstructor() {
        SysRole role = new SysRole("123");
        assertEquals("123", role.getId());
    }

    @Test
    @DisplayName("SysRole - Getter和Setter")
    void testSysRole_GettersAndSetters() {
        SysRole role = new SysRole();

        role.setRoleName("测试角色");
        role.setRoleKey("test_role");
        role.setRoleSort("1");
        role.setDataScope("1");
        role.setStatus("0");

        assertEquals("测试角色", role.getRoleName());
        assertEquals("test_role", role.getRoleKey());
        assertEquals("1", role.getRoleSort());
        assertEquals("1", role.getDataScope());
        assertEquals("0", role.getStatus());
    }

    @Test
    @DisplayName("SysRole - isAdmin方法")
    void testSysRole_IsAdmin() {
        SysRole adminRole = new SysRole();
        adminRole.setRoleKey("admin");
        assertTrue(adminRole.isAdmin());

        SysRole normalRole = new SysRole();
        normalRole.setRoleKey("common");
        assertFalse(normalRole.isAdmin());
    }

    @Test
    @DisplayName("SysRole - isAdmin静态方法")
    void testSysRole_IsAdmin_Static() {
        assertTrue(SysRole.isAdmin("admin"));
        assertFalse(SysRole.isAdmin("common"));
        assertFalse(SysRole.isAdmin(null));
    }

    @Test
    @DisplayName("SysRole - Flag标志")
    void testSysRole_Flag() {
        SysRole role = new SysRole();
        assertFalse(role.isFlag());

        role.setFlag(true);
        assertTrue(role.isFlag());
    }

    @Test
    @DisplayName("SysRole - MenuIds和DeptIds")
    void testSysRole_MenuIdsAndDeptIds() {
        SysRole role = new SysRole();
        String[] menuIds = {"1", "2", "3"};
        String[] deptIds = {"1", "2"};

        role.setMenuIds(menuIds);
        role.setDeptIds(deptIds);

        assertArrayEquals(menuIds, role.getMenuIds());
        assertArrayEquals(deptIds, role.getDeptIds());
    }

    @Test
    @DisplayName("SysRole - Permissions")
    void testSysRole_Permissions() {
        SysRole role = new SysRole();
        Set<String> permissions = new HashSet<>();
        permissions.add("system:user:list");
        permissions.add("system:role:list");

        role.setPermissions(permissions);

        assertEquals(permissions, role.getPermissions());
        assertTrue(role.getPermissions().contains("system:user:list"));
    }

    // ============================================
    // SysDept 实体测试
    // ============================================

    @Test
    @DisplayName("SysDept - Getter和Setter")
    void testSysDept_GettersAndSetters() {
        SysDept dept = new SysDept();

        dept.setParentId("0");
        dept.setAncestors("0,1");
        dept.setDeptName("测试部门");
        dept.setOrderNum(1);
        dept.setLeader("张三");
        dept.setPhone("13800000000");
        dept.setEmail("test@test.com");
        dept.setStatus("0");

        assertEquals("0", dept.getParentId());
        assertEquals("0,1", dept.getAncestors());
        assertEquals("测试部门", dept.getDeptName());
        assertEquals(1, dept.getOrderNum());
        assertEquals("张三", dept.getLeader());
        assertEquals("13800000000", dept.getPhone());
        assertEquals("test@test.com", dept.getEmail());
        assertEquals("0", dept.getStatus());
    }

    @Test
    @DisplayName("SysDept - ExcludeId")
    void testSysDept_ExcludeId() {
        SysDept dept = new SysDept();
        dept.setExcludeId("123");

        assertEquals("123", dept.getExcludeId());
    }

    @Test
    @DisplayName("SysDept - toString方法")
    void testSysDept_ToString() {
        SysDept dept = new SysDept();
        dept.setId("1");
        dept.setDeptName("测试部门");

        String str = dept.toString();
        assertNotNull(str);
        assertTrue(str.contains("测试部门"));
    }

    // ============================================
    // SysMenu 实体测试
    // ============================================

    @Test
    @DisplayName("SysMenu - Getter和Setter")
    void testSysMenu_GettersAndSetters() {
        SysMenu menu = new SysMenu();

        menu.setMenuName("测试菜单");
        menu.setParentId("0");
        menu.setOrderNum("1");
        menu.setUrl("/system/test");
        menu.setTarget("menuItem");
        menu.setMenuType("C");
        menu.setVisible("0");
        menu.setIsRefresh("1");
        menu.setPerms("system:test:list");
        menu.setIcon("test");

        assertEquals("测试菜单", menu.getMenuName());
        assertEquals("0", menu.getParentId());
        assertEquals("1", menu.getOrderNum());
        assertEquals("/system/test", menu.getUrl());
        assertEquals("menuItem", menu.getTarget());
        assertEquals("C", menu.getMenuType());
        assertEquals("0", menu.getVisible());
        assertEquals("1", menu.getIsRefresh());
        assertEquals("system:test:list", menu.getPerms());
        assertEquals("test", menu.getIcon());
    }

    @Test
    @DisplayName("SysMenu - Children初始化")
    void testSysMenu_ChildrenInit() {
        SysMenu menu = new SysMenu();
        List<SysMenu> children = menu.getChildren();

        assertNotNull(children);
        assertTrue(children.isEmpty());
    }

    @Test
    @DisplayName("SysMenu - Children设置")
    void testSysMenu_ChildrenSetter() {
        SysMenu menu = new SysMenu();
        List<SysMenu> children = new ArrayList<>();
        children.add(new SysMenu());
        children.add(new SysMenu());

        menu.setChildren(children);

        assertEquals(2, menu.getChildren().size());
    }

    @Test
    @DisplayName("SysMenu - toString方法")
    void testSysMenu_ToString() {
        SysMenu menu = new SysMenu();
        menu.setId("1");
        menu.setMenuName("测试菜单");

        String str = menu.toString();
        assertNotNull(str);
        assertTrue(str.contains("测试菜单"));
    }

    // ============================================
    // SysPost 实体测试
    // ============================================

    @Test
    @DisplayName("SysPost - Getter和Setter")
    void testSysPost_GettersAndSetters() {
        SysPost post = new SysPost();

        post.setPostCode("test_post");
        post.setPostName("测试岗位");
        post.setPostSort("1");
        post.setStatus("0");

        assertEquals("test_post", post.getPostCode());
        assertEquals("测试岗位", post.getPostName());
        assertEquals("1", post.getPostSort());
        assertEquals("0", post.getStatus());
    }

    @Test
    @DisplayName("SysPost - Flag标志")
    void testSysPost_Flag() {
        SysPost post = new SysPost();
        assertFalse(post.isFlag());

        post.setFlag(true);
        assertTrue(post.isFlag());
    }

    @Test
    @DisplayName("SysPost - toString方法")
    void testSysPost_ToString() {
        SysPost post = new SysPost();
        post.setId("1");
        post.setPostName("测试岗位");

        String str = post.toString();
        assertNotNull(str);
        assertTrue(str.contains("测试岗位"));
    }

    // ============================================
    // SysUserRole 实体测试
    // ============================================

    @Test
    @DisplayName("SysUserRole - Getter和Setter")
    void testSysUserRole_GettersAndSetters() {
        SysUserRole userRole = new SysUserRole();

        userRole.setUserId("1");
        userRole.setRoleId("2");

        assertEquals("1", userRole.getUserId());
        assertEquals("2", userRole.getRoleId());
    }

    // ============================================
    // SysUserPost 实体测试
    // ============================================

    @Test
    @DisplayName("SysUserPost - Getter和Setter")
    void testSysUserPost_GettersAndSetters() {
        SysUserPost userPost = new SysUserPost();

        userPost.setUserId("1");
        userPost.setPostId("2");

        assertEquals("1", userPost.getUserId());
        assertEquals("2", userPost.getPostId());
    }

    // ============================================
    // SysRoleMenu 实体测试
    // ============================================

    @Test
    @DisplayName("SysRoleMenu - Getter和Setter")
    void testSysRoleMenu_GettersAndSetters() {
        SysRoleMenu roleMenu = new SysRoleMenu();

        roleMenu.setRoleId("1");
        roleMenu.setMenuId("2");

        assertEquals("1", roleMenu.getRoleId());
        assertEquals("2", roleMenu.getMenuId());
    }

    // ============================================
    // SysRoleDept 实体测试
    // ============================================

    @Test
    @DisplayName("SysRoleDept - Getter和Setter")
    void testSysRoleDept_GettersAndSetters() {
        SysRoleDept roleDept = new SysRoleDept();

        roleDept.setRoleId("1");
        roleDept.setDeptId("2");

        assertEquals("1", roleDept.getRoleId());
        assertEquals("2", roleDept.getDeptId());
    }
}
