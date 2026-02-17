package com.zjjh.fdtemp.beans.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SysOperLog 实体类单元测试
 */
@DisplayName("操作日志实体类测试")
class SysOperLogTest {

    private SysOperLog operLog;

    @BeforeEach
    void setUp() {
        operLog = new SysOperLog();
    }

    @Test
    @DisplayName("测试设置和获取title")
    void testTitle() {
        operLog.setTitle("用户管理");
        assertEquals("用户管理", operLog.getTitle());
    }

    @Test
    @DisplayName("测试设置和获取businessType")
    void testBusinessType() {
        operLog.setBusinessType(1);
        assertEquals(1, operLog.getBusinessType());
    }

    @Test
    @DisplayName("测试设置和获取businessTypes数组")
    void testBusinessTypes() {
        Integer[] types = {1, 2, 3};
        operLog.setBusinessTypes(types);
        assertArrayEquals(types, operLog.getBusinessTypes());
    }

    @Test
    @DisplayName("测试设置和获取method")
    void testMethod() {
        operLog.setMethod("com.zjjh.fdtemp.controller.UserController.list()");
        assertEquals("com.zjjh.fdtemp.controller.UserController.list()", operLog.getMethod());
    }

    @Test
    @DisplayName("测试设置和获取requestMethod")
    void testRequestMethod() {
        operLog.setRequestMethod("POST");
        assertEquals("POST", operLog.getRequestMethod());
    }

    @Test
    @DisplayName("测试设置和获取operatorType")
    void testOperatorType() {
        operLog.setOperatorType(1);
        assertEquals(1, operLog.getOperatorType());
    }

    @Test
    @DisplayName("测试设置和获取operName")
    void testOperName() {
        operLog.setOperName("admin");
        assertEquals("admin", operLog.getOperName());
    }

    @Test
    @DisplayName("测试设置和获取deptName")
    void testDeptName() {
        operLog.setDeptName("研发部");
        assertEquals("研发部", operLog.getDeptName());
    }

    @Test
    @DisplayName("测试设置和获取operUrl")
    void testOperUrl() {
        operLog.setOperUrl("/system/user/list");
        assertEquals("/system/user/list", operLog.getOperUrl());
    }

    @Test
    @DisplayName("测试设置和获取operIp")
    void testOperIp() {
        operLog.setOperIp("127.0.0.1");
        assertEquals("127.0.0.1", operLog.getOperIp());
    }

    @Test
    @DisplayName("测试设置和获取operLocation")
    void testOperLocation() {
        operLog.setOperLocation("北京市");
        assertEquals("北京市", operLog.getOperLocation());
    }

    @Test
    @DisplayName("测试设置和获取operParam")
    void testOperParam() {
        operLog.setOperParam("{\"pageNum\":1,\"pageSize\":10}");
        assertEquals("{\"pageNum\":1,\"pageSize\":10}", operLog.getOperParam());
    }

    @Test
    @DisplayName("测试设置和获取jsonResult")
    void testJsonResult() {
        operLog.setJsonResult("{\"code\":200,\"msg\":\"成功\"}");
        assertEquals("{\"code\":200,\"msg\":\"成功\"}", operLog.getJsonResult());
    }

    @Test
    @DisplayName("测试设置和获取status")
    void testStatus() {
        operLog.setStatus(0);
        assertEquals(0, operLog.getStatus());
    }

    @Test
    @DisplayName("测试设置和获取errorMsg")
    void testErrorMsg() {
        operLog.setErrorMsg("系统异常");
        assertEquals("系统异常", operLog.getErrorMsg());
    }

    @Test
    @DisplayName("测试设置和获取operTime")
    void testOperTime() {
        Date now = new Date();
        operLog.setOperTime(now);
        assertEquals(now, operLog.getOperTime());
    }

    @Test
    @DisplayName("测试设置和获取costTime")
    void testCostTime() {
        operLog.setCostTime(100L);
        assertEquals(100L, operLog.getCostTime());
    }

    @Test
    @DisplayName("测试继承自BaseEntity的属性")
    void testBaseEntityProperties() {
        operLog.setId("test-id-123");
        operLog.setCreateUser("admin");
        operLog.setCreateTime(new Date());

        assertEquals("test-id-123", operLog.getId());
        assertEquals("admin", operLog.getCreateUser());
        assertNotNull(operLog.getCreateTime());
    }

    @Test
    @DisplayName("测试空值处理")
    void testNullValues() {
        SysOperLog emptyLog = new SysOperLog();

        assertNull(emptyLog.getTitle());
        assertNull(emptyLog.getBusinessType());
        assertNull(emptyLog.getMethod());
        assertNull(emptyLog.getOperName());
        assertNull(emptyLog.getStatus());
        assertNull(emptyLog.getCostTime());
    }

    @Test
    @DisplayName("测试边界值 - costTime为0")
    void testBoundaryCostTimeZero() {
        operLog.setCostTime(0L);
        assertEquals(0L, operLog.getCostTime());
    }

    @Test
    @DisplayName("测试边界值 - costTime为大数值")
    void testBoundaryCostTimeLarge() {
        operLog.setCostTime(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, operLog.getCostTime());
    }

    @Test
    @DisplayName("测试所有业务类型")
    void testAllBusinessTypes() {
        for (int i = 0; i <= 9; i++) {
            operLog.setBusinessType(i);
            assertEquals(i, operLog.getBusinessType());
        }
    }

    @Test
    @DisplayName("测试所有操作状态")
    void testAllStatus() {
        operLog.setStatus(0);
        assertEquals(0, operLog.getStatus());

        operLog.setStatus(1);
        assertEquals(1, operLog.getStatus());
    }
}
