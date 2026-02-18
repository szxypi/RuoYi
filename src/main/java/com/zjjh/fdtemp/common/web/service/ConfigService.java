package com.zjjh.fdtemp.common.web.service;

import com.zjjh.fdtemp.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 系统参数配置查询服务，供模板或接口层调用
 *
 * @author szx
 */
@Service("config")
public class ConfigService {
    @Autowired
    private SysConfigService configService;

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    public String getKey(String configKey) {
        return configService.selectConfigByKey(configKey);
    }
}
