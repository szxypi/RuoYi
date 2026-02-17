package com.zjjh.fdtemp.beans.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SysEntitiesTest {

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
    }

    @Test
    @DisplayName("SysUser - Dept对象延迟初始化")
    void testSysUser_DeptLazyInit() {
        SysUser user = new SysUser();
        SysDept dept = user.getDept();
        assertNotNull(dept);
    }

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
    @DisplayName("SysDept - Getter和Setter")
    void testSysDept_GettersAndSetters() {
        SysDept dept = new SysDept();
        dept.setParentId("0");
        dept.setAncestors("0,1");
        dept.setDeptName("测试部门");
        dept.setOrderNum(1);
        dept.setLeader("张三");
        dept.setStatus("0");
        assertEquals("0", dept.getParentId());
        assertEquals("0,1", dept.getAncestors());
        assertEquals("测试部门", dept.getDeptName());
        assertEquals(1, dept.getOrderNum());
    }

    @Test
    @DisplayName("SysMenu - Getter和Setter")
    void testSysMenu_GettersAndSetters() {
        SysMenu menu = new SysMenu();
        menu.setMenuName("测试菜单");
        menu.setParentId("0");
        menu.setOrderNum("1");
        menu.setUrl("/system/test");
        menu.setMenuType("C");
        menu.setPerms("system:test:list");
        assertEquals("测试菜单", menu.getMenuName());
        assertEquals("0", menu.getParentId());
        assertEquals("1", menu.getOrderNum());
        assertEquals("/system/test", menu.getUrl());
        assertEquals("C", menu.getMenuType());
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
    @DisplayName("SysUserRole - Getter和Setter")
    void testSysUserRole_GettersAndSetters() {
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId("1");
        userRole.setRoleId("2");
        assertEquals("1", userRole.getUserId());
        assertEquals("2", userRole.getRoleId());
    }

    @Test
    @DisplayName("SysUserPost - Getter和Setter")
    void testSysUserPost_GettersAndSetters() {
        SysUserPost userPost = new SysUserPost();
        userPost.setUserId("1");
        userPost.setPostId("2");
        assertEquals("1", userPost.getUserId());
        assertEquals("2", userPost.getPostId());
    }

    @Test
    @DisplayName("SysRoleMenu - Getter和Setter")
    void testSysRoleMenu_GettersAndSetters() {
        SysRoleMenu roleMenu = new SysRoleMenu();
        roleMenu.setRoleId("1");
        roleMenu.setMenuId("2");
        assertEquals("1", roleMenu.getRoleId());
        assertEquals("2", roleMenu.getMenuId());
    }

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
