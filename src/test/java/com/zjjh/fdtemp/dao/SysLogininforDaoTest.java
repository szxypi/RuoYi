package com.zjjh.fdtemp.dao;

import com.zjjh.fdtemp.beans.entity.SysLogininfor;
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
 * SysLogininforDao 数据访问层单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("登录日志DAO接口测试")
class SysLogininforDaoTest {

    private SysLogininforDao logininforDao;

    private SysLogininfor testLogininfor;

    @BeforeEach
    void setUp() {
        logininforDao = mock(SysLogininforDao.class);
        testLogininfor = new SysLogininfor();
        testLogininfor.setId("1");
        testLogininfor.setLoginName("admin");
        testLogininfor.setIpaddr("127.0.0.1");
        testLogininfor.setStatus("0");
        testLogininfor.setMsg("登录成功");
    }

    @Test
    @DisplayName("测试新增登录日志")
    void testInsertLogininfor() {
        doNothing().when(logininforDao).insertLogininfor(any(SysLogininfor.class));

        logininforDao.insertLogininfor(testLogininfor);

        verify(logininforDao, times(1)).insertLogininfor(testLogininfor);
    }

    @Test
    @DisplayName("测试查询登录日志列表")
    void testSelectLogininforList() {
        List<SysLogininfor> expectedList = Arrays.asList(testLogininfor);
        when(logininforDao.selectLogininforList(any(SysLogininfor.class))).thenReturn(expectedList);

        List<SysLogininfor> result = logininforDao.selectLogininforList(new SysLogininfor());

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(logininforDao, times(1)).selectLogininforList(any(SysLogininfor.class));
    }

    @Test
    @DisplayName("测试查询登录日志列表 - 空列表")
    void testSelectLogininforListEmpty() {
        when(logininforDao.selectLogininforList(any())).thenReturn(Collections.emptyList());

        List<SysLogininfor> result = logininforDao.selectLogininforList(new SysLogininfor());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("测试批量删除登录日志")
    void testDeleteLogininforByIds() {
        String[] ids = {"1", "2", "3"};
        when(logininforDao.deleteLogininforByIds(ids)).thenReturn(3);

        int result = logininforDao.deleteLogininforByIds(ids);

        assertEquals(3, result);
        verify(logininforDao, times(1)).deleteLogininforByIds(ids);
    }

    @Test
    @DisplayName("测试清空登录日志")
    void testCleanLogininfor() {
        when(logininforDao.cleanLogininfor()).thenReturn(1);

        int result = logininforDao.cleanLogininfor();

        assertEquals(1, result);
        verify(logininforDao, times(1)).cleanLogininfor();
    }

    @Test
    @DisplayName("测试接口方法签名")
    void testInterfaceMethodSignatures() throws NoSuchMethodException {
        assertNotNull(SysLogininforDao.class.getMethod("insertLogininfor", SysLogininfor.class));
        assertNotNull(SysLogininforDao.class.getMethod("selectLogininforList", SysLogininfor.class));
        assertNotNull(SysLogininforDao.class.getMethod("deleteLogininforByIds", String[].class));
        assertNotNull(SysLogininforDao.class.getMethod("cleanLogininfor"));
    }

    @Test
    @DisplayName("测试多次调用插入")
    void testMultipleInsertCalls() {
        doNothing().when(logininforDao).insertLogininfor(any());

        logininforDao.insertLogininfor(testLogininfor);
        logininforDao.insertLogininfor(testLogininfor);

        verify(logininforDao, times(2)).insertLogininfor(any());
    }

    @Test
    @DisplayName("测试删除返回0")
    void testDeleteReturnsZero() {
        String[] ids = {"nonexistent"};
        when(logininforDao.deleteLogininforByIds(ids)).thenReturn(0);

        int result = logininforDao.deleteLogininforByIds(ids);

        assertEquals(0, result);
    }

    @Test
    @DisplayName("测试清空返回0")
    void testCleanReturnsZero() {
        when(logininforDao.cleanLogininfor()).thenReturn(0);

        int result = logininforDao.cleanLogininfor();

        assertEquals(0, result);
    }

    @Test
    @DisplayName("测试查询返回多条记录")
    void testSelectMultipleRecords() {
        SysLogininfor log1 = new SysLogininfor();
        log1.setId("1");
        log1.setLoginName("admin");

        SysLogininfor log2 = new SysLogininfor();
        log2.setId("2");
        log2.setLoginName("user1");

        List<SysLogininfor> expectedList = Arrays.asList(log1, log2);
        when(logininforDao.selectLogininforList(any())).thenReturn(expectedList);

        List<SysLogininfor> result = logininforDao.selectLogininforList(new SysLogininfor());

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("测试插入失败日志")
    void testInsertFailedLogin() {
        SysLogininfor failedLog = new SysLogininfor();
        failedLog.setLoginName("hacker");
        failedLog.setStatus("1");
        failedLog.setMsg("密码错误");

        doNothing().when(logininforDao).insertLogininfor(any());

        logininforDao.insertLogininfor(failedLog);

        verify(logininforDao, times(1)).insertLogininfor(failedLog);
    }
}
