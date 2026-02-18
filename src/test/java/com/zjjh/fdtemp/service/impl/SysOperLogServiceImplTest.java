package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.beans.entity.SysOperLog;
import com.zjjh.fdtemp.common.core.text.Convert;
import com.zjjh.fdtemp.dao.SysOperLogDao;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SysOperLogServiceImpl 服务层单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("操作日志服务测试")
class SysOperLogServiceImplTest {

    @Mock
    private SysOperLogDao operLogMapper;

    @InjectMocks
    private SysOperLogServiceImpl operLogService;

    private SysOperLog testOperLog;

    @BeforeEach
    void setUp() {
        testOperLog = new SysOperLog();
        testOperLog.setId("test-id-1");
        testOperLog.setTitle("用户管理");
        testOperLog.setBusinessType(1);
        testOperLog.setOperName("admin");
        testOperLog.setStatus(0);
    }

    @Test
    @DisplayName("测试新增操作日志")
    void testInsertOperlog() {
        doNothing().when(operLogMapper).insertOperlog(any(SysOperLog.class));

        operLogService.insertOperlog(testOperLog);

        verify(operLogMapper, times(1)).insertOperlog(testOperLog);
    }

    @Test
    @DisplayName("测试新增操作日志 - 空对象")
    void testInsertOperlogNull() {
        doNothing().when(operLogMapper).insertOperlog(null);

        operLogService.insertOperlog(null);

        verify(operLogMapper, times(1)).insertOperlog(null);
    }

    @Test
    @DisplayName("测试查询操作日志列表")
    void testSelectOperLogList() {
        List<SysOperLog> expectedList = Arrays.asList(testOperLog);
        when(operLogMapper.selectOperLogList(any(SysOperLog.class))).thenReturn(expectedList);

        List<SysOperLog> result = operLogService.selectOperLogList(testOperLog);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("用户管理", result.get(0).getTitle());
        verify(operLogMapper, times(1)).selectOperLogList(testOperLog);
    }

    @Test
    @DisplayName("测试查询操作日志列表 - 空列表")
    void testSelectOperLogListEmpty() {
        when(operLogMapper.selectOperLogList(any(SysOperLog.class))).thenReturn(Collections.emptyList());

        List<SysOperLog> result = operLogService.selectOperLogList(new SysOperLog());

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(operLogMapper, times(1)).selectOperLogList(any(SysOperLog.class));
    }

    @Test
    @DisplayName("测试根据ID删除操作日志 - 单个ID")
    void testDeleteOperLogByIdsSingle() {
        String ids = "1";
        String[] idArray = Convert.toStrArray(ids);
        when(operLogMapper.deleteOperLogByIds(idArray)).thenReturn(1);

        int result = operLogService.deleteOperLogByIds(ids);

        assertEquals(1, result);
        verify(operLogMapper, times(1)).deleteOperLogByIds(idArray);
    }

    @Test
    @DisplayName("测试根据ID删除操作日志 - 多个ID")
    void testDeleteOperLogByIdsMultiple() {
        String ids = "1,2,3";
        String[] idArray = Convert.toStrArray(ids);
        when(operLogMapper.deleteOperLogByIds(idArray)).thenReturn(3);

        int result = operLogService.deleteOperLogByIds(ids);

        assertEquals(3, result);
        verify(operLogMapper, times(1)).deleteOperLogByIds(idArray);
    }

    @Test
    @DisplayName("测试根据ID删除操作日志 - 空字符串")
    void testDeleteOperLogByIdsEmpty() {
        String ids = "";
        String[] idArray = Convert.toStrArray(ids);
        when(operLogMapper.deleteOperLogByIds(idArray)).thenReturn(0);

        int result = operLogService.deleteOperLogByIds(ids);

        assertEquals(0, result);
        verify(operLogMapper, times(1)).deleteOperLogByIds(idArray);
    }

    @Test
    @DisplayName("测试根据ID查询操作日志详情")
    void testSelectOperLogById() {
        when(operLogMapper.selectOperLogById("1")).thenReturn(testOperLog);

        SysOperLog result = operLogService.selectOperLogById("1");

        assertNotNull(result);
        assertEquals("用户管理", result.getTitle());
        assertEquals("admin", result.getOperName());
        verify(operLogMapper, times(1)).selectOperLogById("1");
    }

    @Test
    @DisplayName("测试根据ID查询操作日志详情 - 不存在")
    void testSelectOperLogByIdNotFound() {
        when(operLogMapper.selectOperLogById("999")).thenReturn(null);

        SysOperLog result = operLogService.selectOperLogById("999");

        assertNull(result);
        verify(operLogMapper, times(1)).selectOperLogById("999");
    }

    @Test
    @DisplayName("测试清空操作日志")
    void testCleanOperLog() {
        doNothing().when(operLogMapper).cleanOperLog();

        operLogService.cleanOperLog();

        verify(operLogMapper, times(1)).cleanOperLog();
    }

    @Test
    @DisplayName("测试查询操作日志列表 - 带条件查询")
    void testSelectOperLogListWithCondition() {
        SysOperLog query = new SysOperLog();
        query.setOperName("admin");
        query.setStatus(0);

        List<SysOperLog> expectedList = Arrays.asList(testOperLog);
        when(operLogMapper.selectOperLogList(query)).thenReturn(expectedList);

        List<SysOperLog> result = operLogService.selectOperLogList(query);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(operLogMapper, times(1)).selectOperLogList(query);
    }

    @Test
    @DisplayName("测试查询操作日志列表 - 多条记录")
    void testSelectOperLogListMultiple() {
        SysOperLog log1 = new SysOperLog();
        log1.setId("1");
        log1.setTitle("用户管理");

        SysOperLog log2 = new SysOperLog();
        log2.setId("2");
        log2.setTitle("角色管理");

        SysOperLog log3 = new SysOperLog();
        log3.setId("3");
        log3.setTitle("菜单管理");

        List<SysOperLog> expectedList = Arrays.asList(log1, log2, log3);
        when(operLogMapper.selectOperLogList(any())).thenReturn(expectedList);

        List<SysOperLog> result = operLogService.selectOperLogList(new SysOperLog());

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("测试Convert.toStrArray转换")
    void testConvertToStrArray() {
        String ids = "1,2,3,4,5";
        String[] result = Convert.toStrArray(ids);

        assertEquals(5, result.length);
        assertEquals("1", result[0]);
        assertEquals("5", result[4]);
    }

    @Test
    @DisplayName("测试Convert.toStrArray - 空字符串")
    void testConvertToStrArrayEmpty() {
        String ids = "";
        String[] result = Convert.toStrArray(ids);

        // Convert.toStrArray 在输入为空时返回空数组
        assertEquals(0, result.length);
    }
}
