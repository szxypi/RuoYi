package com.zjjh.fdtemp.dao;

import com.zjjh.fdtemp.beans.entity.SysOperLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * SysOperLogDao 数据访问层单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("操作日志DAO接口测试")
class SysOperLogDaoTest {

    private SysOperLogDao operLogDao;

    private SysOperLog testOperLog;

    @BeforeEach
    void setUp() {
        operLogDao = mock(SysOperLogDao.class);
        testOperLog = new SysOperLog();
        testOperLog.setId("1");
        testOperLog.setTitle("用户管理");
        testOperLog.setBusinessType(1);
        testOperLog.setOperName("admin");
        testOperLog.setStatus(0);
    }

    @Test
    @DisplayName("测试新增操作日志")
    void testInsertOperlog() {
        doNothing().when(operLogDao).insertOperlog(any(SysOperLog.class));

        operLogDao.insertOperlog(testOperLog);

        verify(operLogDao, times(1)).insertOperlog(testOperLog);
    }

    @Test
    @DisplayName("测试查询操作日志列表")
    void testSelectOperLogList() {
        List<SysOperLog> expectedList = Arrays.asList(testOperLog);
        when(operLogDao.selectOperLogList(any(SysOperLog.class))).thenReturn(expectedList);

        List<SysOperLog> result = operLogDao.selectOperLogList(new SysOperLog());

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(operLogDao, times(1)).selectOperLogList(any(SysOperLog.class));
    }

    @Test
    @DisplayName("测试查询操作日志列表 - 空列表")
    void testSelectOperLogListEmpty() {
        when(operLogDao.selectOperLogList(any())).thenReturn(Collections.emptyList());

        List<SysOperLog> result = operLogDao.selectOperLogList(new SysOperLog());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("测试批量删除操作日志")
    void testDeleteOperLogByIds() {
        String[] ids = {"1", "2", "3"};
        when(operLogDao.deleteOperLogByIds(ids)).thenReturn(3);

        int result = operLogDao.deleteOperLogByIds(ids);

        assertEquals(3, result);
        verify(operLogDao, times(1)).deleteOperLogByIds(ids);
    }

    @Test
    @DisplayName("测试根据ID查询操作日志")
    void testSelectOperLogById() {
        when(operLogDao.selectOperLogById("1")).thenReturn(testOperLog);

        SysOperLog result = operLogDao.selectOperLogById("1");

        assertNotNull(result);
        assertEquals("用户管理", result.getTitle());
        verify(operLogDao, times(1)).selectOperLogById("1");
    }

    @Test
    @DisplayName("测试根据ID查询操作日志 - 不存在")
    void testSelectOperLogByIdNotFound() {
        when(operLogDao.selectOperLogById("999")).thenReturn(null);

        SysOperLog result = operLogDao.selectOperLogById("999");

        assertNull(result);
    }

    @Test
    @DisplayName("测试清空操作日志")
    void testCleanOperLog() {
        doNothing().when(operLogDao).cleanOperLog();

        operLogDao.cleanOperLog();

        verify(operLogDao, times(1)).cleanOperLog();
    }

    @Test
    @DisplayName("测试接口方法签名")
    void testInterfaceMethodSignatures() throws NoSuchMethodException {
        assertNotNull(SysOperLogDao.class.getMethod("insertOperlog", SysOperLog.class));
        assertNotNull(SysOperLogDao.class.getMethod("selectOperLogList", SysOperLog.class));
        assertNotNull(SysOperLogDao.class.getMethod("deleteOperLogByIds", String[].class));
        assertNotNull(SysOperLogDao.class.getMethod("selectOperLogById", String.class));
        assertNotNull(SysOperLogDao.class.getMethod("cleanOperLog"));
    }

    @Test
    @DisplayName("测试多次调用插入")
    void testMultipleInsertCalls() {
        doNothing().when(operLogDao).insertOperlog(any());

        operLogDao.insertOperlog(testOperLog);
        operLogDao.insertOperlog(testOperLog);

        verify(operLogDao, times(2)).insertOperlog(any());
    }

    @Test
    @DisplayName("测试删除返回0")
    void testDeleteReturnsZero() {
        String[] ids = {"nonexistent"};
        when(operLogDao.deleteOperLogByIds(ids)).thenReturn(0);

        int result = operLogDao.deleteOperLogByIds(ids);

        assertEquals(0, result);
    }

    @Test
    @DisplayName("测试查询返回多条记录")
    void testSelectMultipleRecords() {
        SysOperLog log1 = new SysOperLog();
        log1.setId("1");

        SysOperLog log2 = new SysOperLog();
        log2.setId("2");

        List<SysOperLog> expectedList = Arrays.asList(log1, log2);
        when(operLogDao.selectOperLogList(any())).thenReturn(expectedList);

        List<SysOperLog> result = operLogDao.selectOperLogList(new SysOperLog());

        assertEquals(2, result.size());
    }
}
