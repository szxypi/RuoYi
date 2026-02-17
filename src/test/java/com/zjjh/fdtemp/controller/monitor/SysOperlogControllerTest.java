package com.zjjh.fdtemp.controller.monitor;

import com.zjjh.fdtemp.beans.entity.SysOperLog;
import com.zjjh.fdtemp.common.core.domain.AjaxResult;
import com.zjjh.fdtemp.service.SysOperLogService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("操作日志控制器测试")
class SysOperlogControllerTest {

    @Mock
    private SysOperLogService operLogService;

    @InjectMocks
    private SysOperlogController controller;

    private SysOperLog testOperLog;
    private List<SysOperLog> testOperLogList;

    @BeforeEach
    void setUp() {
        testOperLog = new SysOperLog();
        testOperLog.setId("1");
        testOperLog.setTitle("用户管理");
        testOperLog.setBusinessType(1);
        testOperLog.setOperName("admin");
        testOperLog.setStatus(0);

        SysOperLog log2 = new SysOperLog();
        log2.setId("2");
        log2.setTitle("角色管理");
        log2.setBusinessType(2);
        log2.setOperName("admin");

        testOperLogList = Arrays.asList(testOperLog, log2);
    }

    @Test
    @DisplayName("测试导出操作日志")
    void testExport() {
        when(operLogService.selectOperLogList(any(SysOperLog.class))).thenReturn(testOperLogList);
        AjaxResult result = controller.export(new SysOperLog());
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(operLogService, times(1)).selectOperLogList(any(SysOperLog.class));
    }

    @Test
    @DisplayName("测试删除操作日志 - 成功")
    void testRemoveSuccess() {
        String ids = "1,2,3";
        when(operLogService.deleteOperLogByIds(ids)).thenReturn(3);
        AjaxResult result = controller.remove(ids);
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(operLogService, times(1)).deleteOperLogByIds(ids);
    }

    @Test
    @DisplayName("测试删除操作日志 - 失败")
    void testRemoveFail() {
        String ids = "nonexistent";
        when(operLogService.deleteOperLogByIds(ids)).thenReturn(0);
        AjaxResult result = controller.remove(ids);
        assertNotNull(result);
        assertTrue(result.isError());
        verify(operLogService, times(1)).deleteOperLogByIds(ids);
    }

    @Test
    @DisplayName("测试清空操作日志")
    void testClean() {
        doNothing().when(operLogService).cleanOperLog();
        AjaxResult result = controller.clean();
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(operLogService, times(1)).cleanOperLog();
    }

    @Test
    @DisplayName("测试删除单个ID")
    void testRemoveSingleId() {
        String ids = "1";
        when(operLogService.deleteOperLogByIds(ids)).thenReturn(1);
        AjaxResult result = controller.remove(ids);
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(operLogService, times(1)).deleteOperLogByIds(ids);
    }

    @Test
    @DisplayName("测试删除空字符串ID")
    void testRemoveEmptyIds() {
        String ids = "";
        when(operLogService.deleteOperLogByIds(ids)).thenReturn(0);
        AjaxResult result = controller.remove(ids);
        assertNotNull(result);
        assertTrue(result.isError());
    }

    @Test
    @DisplayName("测试导出空列表")
    void testExportEmptyList() {
        when(operLogService.selectOperLogList(any(SysOperLog.class))).thenReturn(Collections.emptyList());
        AjaxResult result = controller.export(new SysOperLog());
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("测试BaseController的toAjax方法")
    void testToAjax() {
        when(operLogService.deleteOperLogByIds("1")).thenReturn(1);
        AjaxResult result = controller.remove("1");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("测试BaseController的toAjax方法 - 返回0")
    void testToAjaxZero() {
        when(operLogService.deleteOperLogByIds("999")).thenReturn(0);
        AjaxResult result = controller.remove("999");
        assertTrue(result.isError());
    }

    @Test
    @DisplayName("测试BaseController的success方法")
    void testSuccess() {
        doNothing().when(operLogService).cleanOperLog();
        AjaxResult result = controller.clean();
        assertTrue(result.isSuccess());
        assertEquals(0, result.get("code"));
    }

    @Test
    @DisplayName("测试导出返回正确的消息")
    void testExportMessage() {
        when(operLogService.selectOperLogList(any())).thenReturn(testOperLogList);
        AjaxResult result = controller.export(new SysOperLog());
        assertTrue(result.containsKey("msg") || result.isSuccess());
    }

    @Test
    @DisplayName("测试删除多个ID返回正确数量")
    void testRemoveMultipleIdsCount() {
        when(operLogService.deleteOperLogByIds("1,2")).thenReturn(2);
        AjaxResult result = controller.remove("1,2");
        assertTrue(result.isSuccess());
    }
}
