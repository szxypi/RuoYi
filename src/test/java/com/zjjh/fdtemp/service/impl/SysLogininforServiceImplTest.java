package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.beans.entity.SysLogininfor;
import com.zjjh.fdtemp.common.core.text.Convert;
import com.zjjh.fdtemp.dao.SysLogininforDao;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SysLogininforServiceImpl 服务层单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("登录日志服务测试")
class SysLogininforServiceImplTest {

    @Mock
    private SysLogininforDao logininforMapper;

    @InjectMocks
    private SysLogininforServiceImpl logininforService;

    private SysLogininfor testLogininfor;

    @BeforeEach
    void setUp() {
        testLogininfor = new SysLogininfor();
        testLogininfor.setId("test-id-1");
        testLogininfor.setLoginName("admin");
        testLogininfor.setIpaddr("127.0.0.1");
        testLogininfor.setStatus("0");
        testLogininfor.setMsg("登录成功");
        testLogininfor.setBrowser("Chrome 100.0");
        testLogininfor.setOs("Windows 10");
    }

    @Test
    @DisplayName("测试新增登录日志")
    void testInsertLogininfor() {
        doNothing().when(logininforMapper).insertLogininfor(any(SysLogininfor.class));

        logininforService.insertLogininfor(testLogininfor);

        verify(logininforMapper, times(1)).insertLogininfor(testLogininfor);
    }

    @Test
    @DisplayName("测试新增登录日志 - 空对象")
    void testInsertLogininforNull() {
        doNothing().when(logininforMapper).insertLogininfor(null);

        logininforService.insertLogininfor(null);

        verify(logininforMapper, times(1)).insertLogininfor(null);
    }

    @Test
    @DisplayName("测试新增登录日志 - 登录失败场景")
    void testInsertLogininforFailed() {
        SysLogininfor failedLog = new SysLogininfor();
        failedLog.setLoginName("hacker");
        failedLog.setIpaddr("192.168.1.100");
        failedLog.setStatus("1");
        failedLog.setMsg("用户名或密码错误");

        doNothing().when(logininforMapper).insertLogininfor(any(SysLogininfor.class));

        logininforService.insertLogininfor(failedLog);

        verify(logininforMapper, times(1)).insertLogininfor(failedLog);
    }

    @Test
    @DisplayName("测试查询登录日志列表")
    void testSelectLogininforList() {
        List<SysLogininfor> expectedList = Arrays.asList(testLogininfor);
        when(logininforMapper.selectLogininforList(any(SysLogininfor.class))).thenReturn(expectedList);

        List<SysLogininfor> result = logininforService.selectLogininforList(testLogininfor);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("admin", result.get(0).getLoginName());
        assertEquals("登录成功", result.get(0).getMsg());
        verify(logininforMapper, times(1)).selectLogininforList(testLogininfor);
    }

    @Test
    @DisplayName("测试查询登录日志列表 - 空列表")
    void testSelectLogininforListEmpty() {
        when(logininforMapper.selectLogininforList(any(SysLogininfor.class))).thenReturn(Collections.emptyList());

        List<SysLogininfor> result = logininforService.selectLogininforList(new SysLogininfor());

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(logininforMapper, times(1)).selectLogininforList(any(SysLogininfor.class));
    }

    @Test
    @DisplayName("测试根据ID删除登录日志 - 单个ID")
    void testDeleteLogininforByIdsSingle() {
        String ids = "1";
        String[] idArray = Convert.toStrArray(ids);
        when(logininforMapper.deleteLogininforByIds(idArray)).thenReturn(1);

        int result = logininforService.deleteLogininforByIds(ids);

        assertEquals(1, result);
        verify(logininforMapper, times(1)).deleteLogininforByIds(idArray);
    }

    @Test
    @DisplayName("测试根据ID删除登录日志 - 多个ID")
    void testDeleteLogininforByIdsMultiple() {
        String ids = "1,2,3";
        String[] idArray = Convert.toStrArray(ids);
        when(logininforMapper.deleteLogininforByIds(idArray)).thenReturn(3);

        int result = logininforService.deleteLogininforByIds(ids);

        assertEquals(3, result);
        verify(logininforMapper, times(1)).deleteLogininforByIds(idArray);
    }

    @Test
    @DisplayName("测试根据ID删除登录日志 - 空字符串")
    void testDeleteLogininforByIdsEmpty() {
        String ids = "";
        String[] idArray = Convert.toStrArray(ids);
        when(logininforMapper.deleteLogininforByIds(idArray)).thenReturn(0);

        int result = logininforService.deleteLogininforByIds(ids);

        assertEquals(0, result);
        verify(logininforMapper, times(1)).deleteLogininforByIds(idArray);
    }

    @Test
    @DisplayName("测试清空登录日志")
    void testCleanLogininfor() {
        when(logininforMapper.cleanLogininfor()).thenReturn(1);

        logininforService.cleanLogininfor();

        verify(logininforMapper, times(1)).cleanLogininfor();
    }

    @Test
    @DisplayName("测试查询登录日志列表 - 带用户名条件")
    void testSelectLogininforListWithLoginName() {
        SysLogininfor query = new SysLogininfor();
        query.setLoginName("admin");

        List<SysLogininfor> expectedList = Arrays.asList(testLogininfor);
        when(logininforMapper.selectLogininforList(query)).thenReturn(expectedList);

        List<SysLogininfor> result = logininforService.selectLogininforList(query);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("admin", result.get(0).getLoginName());
        verify(logininforMapper, times(1)).selectLogininforList(query);
    }

    @Test
    @DisplayName("测试查询登录日志列表 - 带状态条件")
    void testSelectLogininforListWithStatus() {
        SysLogininfor query = new SysLogininfor();
        query.setStatus("0");

        List<SysLogininfor> expectedList = Arrays.asList(testLogininfor);
        when(logininforMapper.selectLogininforList(query)).thenReturn(expectedList);

        List<SysLogininfor> result = logininforService.selectLogininforList(query);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(logininforMapper, times(1)).selectLogininforList(query);
    }

    @Test
    @DisplayName("测试查询登录日志列表 - 多条记录")
    void testSelectLogininforListMultiple() {
        SysLogininfor log1 = new SysLogininfor();
        log1.setId("1");
        log1.setLoginName("admin");

        SysLogininfor log2 = new SysLogininfor();
        log2.setId("2");
        log2.setLoginName("user1");

        SysLogininfor log3 = new SysLogininfor();
        log3.setId("3");
        log3.setLoginName("user2");

        List<SysLogininfor> expectedList = Arrays.asList(log1, log2, log3);
        when(logininforMapper.selectLogininforList(any())).thenReturn(expectedList);

        List<SysLogininfor> result = logininforService.selectLogininforList(new SysLogininfor());

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("测试查询登录日志列表 - 包含失败记录")
    void testSelectLogininforListWithFailedRecords() {
        SysLogininfor successLog = new SysLogininfor();
        successLog.setStatus("0");
        successLog.setMsg("登录成功");

        SysLogininfor failedLog = new SysLogininfor();
        failedLog.setStatus("1");
        failedLog.setMsg("密码错误");

        List<SysLogininfor> expectedList = Arrays.asList(successLog, failedLog);
        when(logininforMapper.selectLogininforList(any())).thenReturn(expectedList);

        List<SysLogininfor> result = logininforService.selectLogininforList(new SysLogininfor());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("0", result.get(0).getStatus());
        assertEquals("1", result.get(1).getStatus());
    }

    @Test
    @DisplayName("测试删除返回0")
    void testDeleteLogininforByIdsZero() {
        String ids = "nonexistent";
        String[] idArray = Convert.toStrArray(ids);
        when(logininforMapper.deleteLogininforByIds(idArray)).thenReturn(0);

        int result = logininforService.deleteLogininforByIds(ids);

        assertEquals(0, result);
    }
}
