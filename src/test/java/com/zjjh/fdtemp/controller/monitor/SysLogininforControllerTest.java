package com.zjjh.fdtemp.controller.monitor;

import com.zjjh.fdtemp.beans.entity.SysLogininfor;
import com.zjjh.fdtemp.common.core.domain.AjaxResult;
import com.zjjh.fdtemp.service.SysLogininforService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("登录日志控制器测试")
class SysLogininforControllerTest {

    @Mock
    private SysLogininforService logininforService;

    @InjectMocks
    private SysLogininforController controller;

    private SysLogininfor testLogininfor;
    private List<SysLogininfor> testLogininforList;

    @BeforeEach
    void setUp() {
        testLogininfor = new SysLogininfor();
        testLogininfor.setId("1");
        testLogininfor.setLoginName("admin");
        testLogininfor.setIpaddr("127.0.0.1");
        testLogininfor.setStatus("0");
        testLogininfor.setMsg("登录成功");
        testLogininfor.setBrowser("Chrome 100.0");
        testLogininfor.setOs("Windows 10");

        SysLogininfor log2 = new SysLogininfor();
        log2.setId("2");
        log2.setLoginName("user1");
        log2.setIpaddr("192.168.1.100");
        log2.setStatus("1");
        log2.setMsg("密码错误");

        testLogininforList = Arrays.asList(testLogininfor, log2);
    }

    @Test
    @DisplayName("测试导出登录日志")
    void testExport() {
        when(logininforService.selectLogininforList(any(SysLogininfor.class))).thenReturn(testLogininforList);
        AjaxResult result = controller.export(new SysLogininfor());
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(logininforService, times(1)).selectLogininforList(any(SysLogininfor.class));
    }

    @Test
    @DisplayName("测试删除登录日志 - 成功")
    void testRemoveSuccess() {
        String ids = "1,2,3";
        when(logininforService.deleteLogininforByIds(ids)).thenReturn(3);
        AjaxResult result = controller.remove(ids);
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(logininforService, times(1)).deleteLogininforByIds(ids);
    }

    @Test
    @DisplayName("测试删除登录日志 - 失败")
    void testRemoveFail() {
        String ids = "nonexistent";
        when(logininforService.deleteLogininforByIds(ids)).thenReturn(0);
        AjaxResult result = controller.remove(ids);
        assertNotNull(result);
        assertTrue(result.isError());
        verify(logininforService, times(1)).deleteLogininforByIds(ids);
    }

    @Test
    @DisplayName("测试清空登录日志")
    void testClean() {
        doNothing().when(logininforService).cleanLogininfor();
        AjaxResult result = controller.clean();
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(logininforService, times(1)).cleanLogininfor();
    }

    @Test
    @DisplayName("测试解锁用户")
    void testUnlock() {
        AjaxResult result = controller.unlock("admin");
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("测试解锁用户 - 空用户名")
    void testUnlockEmpty() {
        AjaxResult result = controller.unlock("");
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("测试删除单个ID")
    void testRemoveSingleId() {
        String ids = "1";
        when(logininforService.deleteLogininforByIds(ids)).thenReturn(1);
        AjaxResult result = controller.remove(ids);
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(logininforService, times(1)).deleteLogininforByIds(ids);
    }

    @Test
    @DisplayName("测试删除空字符串ID")
    void testRemoveEmptyIds() {
        String ids = "";
        when(logininforService.deleteLogininforByIds(ids)).thenReturn(0);
        AjaxResult result = controller.remove(ids);
        assertNotNull(result);
        assertTrue(result.isError());
    }

    @Test
    @DisplayName("测试导出空列表")
    void testExportEmptyList() {
        when(logininforService.selectLogininforList(any(SysLogininfor.class))).thenReturn(Collections.emptyList());
        AjaxResult result = controller.export(new SysLogininfor());
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("测试解锁任何用户都返回成功")
    void testUnlockAlwaysReturnsSuccess() {
        assertTrue(controller.unlock("admin").isSuccess());
        assertTrue(controller.unlock("user1").isSuccess());
        assertTrue(controller.unlock("test_user").isSuccess());
    }

    @Test
    @DisplayName("测试导出返回正确的状态码")
    void testExportReturnsCorrectCode() {
        when(logininforService.selectLogininforList(any())).thenReturn(testLogininforList);
        AjaxResult result = controller.export(new SysLogininfor());
        assertEquals(0, result.get("code"));
    }

    @Test
    @DisplayName("测试多次删除操作")
    void testMultipleRemoveCalls() {
        when(logininforService.deleteLogininforByIds(anyString())).thenReturn(1);
        controller.remove("1");
        controller.remove("2");
        verify(logininforService, times(2)).deleteLogininforByIds(anyString());
    }
}
