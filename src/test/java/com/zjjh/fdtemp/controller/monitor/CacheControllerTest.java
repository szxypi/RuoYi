package com.zjjh.fdtemp.controller.monitor;

import com.zjjh.fdtemp.common.core.domain.AjaxResult;
import com.zjjh.fdtemp.common.web.service.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("缓存监控控制器测试")
class CacheControllerTest {

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private CacheController controller;

    @Test
    @DisplayName("测试清除指定缓存名称")
    void testClearCacheName() {
        String cacheName = "sys-config";
        doNothing().when(cacheService).clearCacheName(cacheName);
        AjaxResult result = controller.clearCacheName(cacheName);
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(cacheService, times(1)).clearCacheName(cacheName);
    }

    @Test
    @DisplayName("测试清除指定缓存键")
    void testClearCacheKey() {
        String cacheName = "sys-config";
        String cacheKey = "sys_config:test";
        doNothing().when(cacheService).clearCacheKey(cacheName, cacheKey);
        AjaxResult result = controller.clearCacheKey(cacheName, cacheKey);
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(cacheService, times(1)).clearCacheKey(cacheName, cacheKey);
    }

    @Test
    @DisplayName("测试清除所有缓存")
    void testClearAll() {
        doNothing().when(cacheService).clearAll();
        AjaxResult result = controller.clearAll();
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(cacheService, times(1)).clearAll();
    }

    @Test
    @DisplayName("测试返回结果包含正确的状态码")
    void testResponseCode() {
        doNothing().when(cacheService).clearCacheName(any());
        AjaxResult result = controller.clearCacheName("test");
        assertEquals(0, result.get("code"));
    }

    @Test
    @DisplayName("测试多次清除同一缓存")
    void testClearSameCacheMultipleTimes() {
        String cacheName = "sys-dict";
        doNothing().when(cacheService).clearCacheName(cacheName);
        controller.clearCacheName(cacheName);
        controller.clearCacheName(cacheName);
        controller.clearCacheName(cacheName);
        verify(cacheService, times(3)).clearCacheName(cacheName);
    }

    @Test
    @DisplayName("测试清除不同缓存")
    void testClearDifferentCaches() {
        doNothing().when(cacheService).clearCacheName(anyString());
        controller.clearCacheName("sys-config");
        controller.clearCacheName("sys-dict");
        controller.clearCacheName("sys-authCache");
        verify(cacheService, times(3)).clearCacheName(anyString());
    }
}
