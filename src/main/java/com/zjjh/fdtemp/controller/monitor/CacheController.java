package com.zjjh.fdtemp.controller.monitor;

import com.zjjh.fdtemp.common.core.BaseController;
import com.zjjh.fdtemp.common.core.domain.AjaxResult;
import com.zjjh.fdtemp.common.web.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 缓存监控
 *
 * @author szx
 */
@RestController
@RequestMapping("/monitor/cache")
public class CacheController extends BaseController {
    @Autowired
    private CacheService cacheService;

    @PreAuthorize("hasAuthority('monitor:cache:view')")
    @PostMapping("/clearCacheName")
    public AjaxResult clearCacheName(String cacheName) {
        cacheService.clearCacheName(cacheName);
        return AjaxResult.success();
    }

    @PreAuthorize("hasAuthority('monitor:cache:view')")
    @PostMapping("/clearCacheKey")
    public AjaxResult clearCacheKey(String cacheName, String cacheKey) {
        cacheService.clearCacheKey(cacheName, cacheKey);
        return AjaxResult.success();
    }

    @PreAuthorize("hasAuthority('monitor:cache:view')")
    @GetMapping("/clearAll")
    public AjaxResult clearAll() {
        cacheService.clearAll();
        return AjaxResult.success();
    }
}
