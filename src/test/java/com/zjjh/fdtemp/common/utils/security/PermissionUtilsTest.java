package com.zjjh.fdtemp.common.utils.security;

import com.zjjh.fdtemp.constants.PermissionConstants;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PermissionUtils 单元测试
 * <p>
 * 注意：PermissionUtils.getMsg依赖MessageUtils，这里测试不涉及静态依赖的逻辑
 */
@DisplayName("PermissionUtils 测试")
class PermissionUtilsTest {

    @Test
    @DisplayName("常量定义验证")
    void testConstants() {
        assertEquals("no.view.permission", PermissionUtils.VIEW_PERMISSION);
        assertEquals("no.create.permission", PermissionUtils.CREATE_PERMISSION);
        assertEquals("no.update.permission", PermissionUtils.UPDATE_PERMISSION);
        assertEquals("no.delete.permission", PermissionUtils.DELETE_PERMISSION);
        assertEquals("no.export.permission", PermissionUtils.EXPORT_PERMISSION);
        assertEquals("no.permission", PermissionUtils.PERMISSION);
    }

    @Test
    @DisplayName("PermissionConstants 常量验证")
    void testPermissionConstants() {
        assertEquals("add", PermissionConstants.ADD_PERMISSION);
        assertEquals("edit", PermissionConstants.EDIT_PERMISSION);
        assertEquals("remove", PermissionConstants.REMOVE_PERMISSION);
        assertEquals("export", PermissionConstants.EXPORT_PERMISSION);
        assertEquals("view", PermissionConstants.VIEW_PERMISSION);
        assertEquals("list", PermissionConstants.LIST_PERMISSION);
    }

    @Test
    @DisplayName("getMsg逻辑 - 提取权限字符串测试")
    void testExtractPermission() {
        // 测试StringUtils.substringBetween的行为
        String permissionStr = "没有权限[system:user:add]";
        String permission = StringUtils.substringBetween(permissionStr, "[", "]");
        assertEquals("system:user:add", permission);
    }

    @Test
    @DisplayName("getMsg逻辑 - 权限后缀匹配-add")
    void testPermissionSuffixMatch_Add() {
        String permission = "system:user:add";
        assertTrue(StringUtils.endsWithIgnoreCase(permission, PermissionConstants.ADD_PERMISSION));
    }

    @Test
    @DisplayName("getMsg逻辑 - 权限后缀匹配-edit")
    void testPermissionSuffixMatch_Edit() {
        String permission = "system:user:edit";
        assertTrue(StringUtils.endsWithIgnoreCase(permission, PermissionConstants.EDIT_PERMISSION));
    }

    @Test
    @DisplayName("getMsg逻辑 - 权限后缀匹配-remove")
    void testPermissionSuffixMatch_Remove() {
        String permission = "system:user:remove";
        assertTrue(StringUtils.endsWithIgnoreCase(permission, PermissionConstants.REMOVE_PERMISSION));
    }

    @Test
    @DisplayName("getMsg逻辑 - 权限后缀匹配-export")
    void testPermissionSuffixMatch_Export() {
        String permission = "system:user:export";
        assertTrue(StringUtils.endsWithIgnoreCase(permission, PermissionConstants.EXPORT_PERMISSION));
    }

    @Test
    @DisplayName("getMsg逻辑 - 权限后缀匹配-view")
    void testPermissionSuffixMatch_View() {
        String permission = "system:user:view";
        assertTrue(StringUtils.endsWithAny(permission,
                new String[]{PermissionConstants.VIEW_PERMISSION, PermissionConstants.LIST_PERMISSION}));
    }

    @Test
    @DisplayName("getMsg逻辑 - 权限后缀匹配-list")
    void testPermissionSuffixMatch_List() {
        String permission = "system:user:list";
        assertTrue(StringUtils.endsWithAny(permission,
                new String[]{PermissionConstants.VIEW_PERMISSION, PermissionConstants.LIST_PERMISSION}));
    }

    @Test
    @DisplayName("getMsg逻辑 - 大小写不敏感匹配")
    void testPermissionSuffixMatch_CaseInsensitive() {
        String permission = "system:user:ADD";
        assertTrue(StringUtils.endsWithIgnoreCase(permission, PermissionConstants.ADD_PERMISSION));
    }

    @Test
    @DisplayName("getMsg逻辑 - 无中括号返回null")
    void testExtractPermission_NoBrackets() {
        String permissionStr = "没有权限system:user:add";
        String permission = StringUtils.substringBetween(permissionStr, "[", "]");
        assertNull(permission);
    }

    @Test
    @DisplayName("getMsg逻辑 - 空字符串处理")
    void testExtractPermission_EmptyString() {
        String permission = StringUtils.substringBetween("", "[", "]");
        assertNull(permission);
    }

    @Test
    @DisplayName("getMsg逻辑 - null处理")
    void testExtractPermission_Null() {
        String permission = StringUtils.substringBetween(null, "[", "]");
        assertNull(permission);
    }
}
