package com.zjjh.fdtemp.common.web.service;

import com.zjjh.fdtemp.common.utils.CacheUtils;
import com.zjjh.fdtemp.constants.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("缓存服务测试")
class CacheServiceTest {

    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new CacheService();
        clearAllCaches();
    }

    private void clearAllCaches() {
        String[] names = CacheUtils.getCacheNames();
        for (String name : names) {
            CacheUtils.removeAll(name);
        }
    }

    @Test
    @DisplayName("测试获取所有缓存名称 - 空缓存")
    void testGetCacheNamesEmpty() {
        String[] names = cacheService.getCacheNames();
        assertNotNull(names);
    }

    @Test
    @DisplayName("测试获取所有缓存名称 - 有缓存")
    void testGetCacheNamesWithCache() {
        CacheUtils.put("test-cache-1", "key1", "value1");
        CacheUtils.put("test-cache-2", "key2", "value2");
        String[] names = cacheService.getCacheNames();
        assertNotNull(names);
        assertTrue(names.length >= 2);
    }

    @Test
    @DisplayName("测试获取缓存键")
    void testGetCacheKeys() {
        String cacheName = "test-cache-keys";
        CacheUtils.put(cacheName, "key1", "value1");
        CacheUtils.put(cacheName, "key2", "value2");
        Set<String> keys = cacheService.getCacheKeys(cacheName);
        assertNotNull(keys);
        assertTrue(keys.contains("key1"));
        assertTrue(keys.contains("key2"));
    }

    @Test
    @DisplayName("测试获取缓存值")
    void testGetCacheValue() {
        String cacheName = "test-cache-value";
        String key = "test-key";
        String value = "test-value";
        CacheUtils.put(cacheName, key, value);
        Object result = cacheService.getCacheValue(cacheName, key);
        assertEquals(value, result);
    }

    @Test
    @DisplayName("测试清除指定缓存名称")
    void testClearCacheName() {
        String cacheName = "test-clear-cache";
        CacheUtils.put(cacheName, "key1", "value1");
        cacheService.clearCacheName(cacheName);
        assertNull(CacheUtils.get(cacheName, "key1"));
    }

    @Test
    @DisplayName("测试清除指定缓存键")
    void testClearCacheKey() {
        String cacheName = "test-clear-key-cache";
        CacheUtils.put(cacheName, "key1", "value1");
        CacheUtils.put(cacheName, "key2", "value2");
        cacheService.clearCacheKey(cacheName, "key1");
        assertNull(CacheUtils.get(cacheName, "key1"));
        assertEquals("value2", CacheUtils.get(cacheName, "key2"));
    }

    @Test
    @DisplayName("测试清除所有缓存")
    void testClearAll() {
        CacheUtils.put("cache1", "key1", "value1");
        CacheUtils.put("cache2", "key2", "value2");
        cacheService.clearAll();
        assertNull(CacheUtils.get("cache1", "key1"));
        assertNull(CacheUtils.get("cache2", "key2"));
    }

    @Test
    @DisplayName("测试缓存不同类型的值")
    void testCacheDifferentTypes() {
        String cacheName = "test-types-cache";
        CacheUtils.put(cacheName, "string-key", "string-value");
        assertEquals("string-value", cacheService.getCacheValue(cacheName, "string-key"));
        CacheUtils.put(cacheName, "int-key", 123);
        assertEquals(123, cacheService.getCacheValue(cacheName, "int-key"));
    }

    @Test
    @DisplayName("测试更新缓存值")
    void testUpdateCacheValue() {
        String cacheName = "test-update-cache";
        String key = "update-key";
        CacheUtils.put(cacheName, key, "old-value");
        assertEquals("old-value", cacheService.getCacheValue(cacheName, key));
        CacheUtils.put(cacheName, key, "new-value");
        assertEquals("new-value", cacheService.getCacheValue(cacheName, key));
    }

    @Test
    @DisplayName("测试清除不存在的缓存名称")
    void testClearNonexistentCacheName() {
        assertDoesNotThrow(() -> cacheService.clearCacheName("nonexistent-cache"));
    }
}
