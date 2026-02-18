package com.zjjh.fdtemp.common.utils;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache工具类 - 使用ConcurrentHashMap替代ehcache
 *
 * @author szx
 */
public class CacheUtils {
    /**
     * 所有缓存，按cacheName分组
     */
    private static final ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> CACHES = new ConcurrentHashMap<>();

    /**
     * 获取指定cacheName的缓存Map，不存在则创建
     *
     * @param cacheName 缓存名称
     * @return 缓存Map
     */
    public static ConcurrentHashMap<String, Object> getCache(String cacheName) {
        return CACHES.computeIfAbsent(cacheName, k -> new ConcurrentHashMap<>());
    }

    /**
     * 写入缓存
     *
     * @param cacheName 缓存名称
     * @param key       缓存键
     * @param value     缓存值
     */
    public static void put(String cacheName, String key, Object value) {
        if (value != null) {
            getCache(cacheName).put(key, value);
        }
    }

    /**
     * 获取缓存
     *
     * @param cacheName 缓存名称
     * @param key       缓存键
     * @return 缓存值
     */
    public static Object get(String cacheName, String key) {
        ConcurrentHashMap<String, Object> cache = CACHES.get(cacheName);
        if (cache != null) {
            return cache.get(key);
        }
        return null;
    }

    /**
     * 删除缓存
     *
     * @param cacheName 缓存名称
     * @param key       缓存键
     */
    public static void remove(String cacheName, String key) {
        ConcurrentHashMap<String, Object> cache = CACHES.get(cacheName);
        if (cache != null) {
            cache.remove(key);
        }
    }

    /**
     * 清除指定cacheName下的所有缓存
     *
     * @param cacheName 缓存名称
     */
    public static void removeAll(String cacheName) {
        CACHES.remove(cacheName);
    }

    /**
     * 获取所有缓存名称
     *
     * @return 缓存名称数组
     */
    public static String[] getCacheNames() {
        Set<String> names = CACHES.keySet();
        return names.toArray(new String[0]);
    }

    /**
     * 获取指定缓存的所有键名
     *
     * @param cacheName 缓存名称
     * @return 键名集合
     */
    public static Set<String> getCacheKeys(String cacheName) {
        ConcurrentHashMap<String, Object> cache = CACHES.get(cacheName);
        if (cache != null) {
            return cache.keySet();
        }
        return Set.of();
    }
}
