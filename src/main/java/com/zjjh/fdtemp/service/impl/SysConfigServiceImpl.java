package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.beans.entity.SysConfig;
import com.zjjh.fdtemp.common.core.text.Convert;
import com.zjjh.fdtemp.common.exception.ServiceException;
import com.zjjh.fdtemp.common.utils.CacheUtils;
import com.zjjh.fdtemp.common.utils.StringUtils;
import com.zjjh.fdtemp.constants.Constants;
import com.zjjh.fdtemp.constants.UserConstants;
import com.zjjh.fdtemp.dao.SysConfigDao;
import com.zjjh.fdtemp.service.SysConfigService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 参数配置 服务层实现
 *
 * @author szx
 */
@Service
public class SysConfigServiceImpl implements SysConfigService {

    private final SysConfigDao configMapper;

    public SysConfigServiceImpl(SysConfigDao configMapper) {
        this.configMapper = configMapper;
    }

    /**
     * 项目启动时，初始化参数到缓存
     */
    @PostConstruct
    public void init() {
        loadingConfigCache();
    }

    @Override
    public SysConfig selectConfigById(String configId) {
        SysConfig config = new SysConfig();
        config.setId(configId);
        return configMapper.selectConfig(config);
    }

    @Override
    public String selectConfigByKey(String configKey) {
        String configValue = Convert.toStr(CacheUtils.get(Constants.SYS_CONFIG_CACHE, getCacheKey(configKey)));
        if (StringUtils.isNotEmpty(configValue)) {
            return configValue;
        }

        SysConfig query = new SysConfig();
        query.setConfigKey(configKey);
        SysConfig config = configMapper.selectConfig(query);
        if (StringUtils.isNull(config)) {
            return StringUtils.EMPTY;
        }

        CacheUtils.put(Constants.SYS_CONFIG_CACHE, getCacheKey(configKey), config.getConfigValue());
        return config.getConfigValue();
    }

    @Override
    public List<SysConfig> selectConfigList(SysConfig config) {
        return configMapper.selectConfigList(config);
    }

    @Override
    public int insertConfig(SysConfig config) {
        int row = configMapper.insertConfig(config);
        if (row > 0) {
            CacheUtils.put(Constants.SYS_CONFIG_CACHE, getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    @Override
    public int updateConfig(SysConfig config) {
        SysConfig existing = configMapper.selectConfigById(config.getId());
        if (!StringUtils.equals(existing.getConfigKey(), config.getConfigKey())) {
            CacheUtils.remove(Constants.SYS_CONFIG_CACHE, getCacheKey(existing.getConfigKey()));
        }

        int row = configMapper.updateConfig(config);
        if (row > 0) {
            CacheUtils.put(Constants.SYS_CONFIG_CACHE, getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    @Override
    public void deleteConfigByIds(String ids) {
        for (String configId : Convert.toStrArray(ids)) {
            SysConfig config = selectConfigById(configId);
            if (StringUtils.equals(UserConstants.YES, config.getConfigType())) {
                throw new ServiceException(String.format("内置参数【%s】不能删除", config.getConfigKey()));
            }
            configMapper.deleteConfigById(configId);
            CacheUtils.remove(Constants.SYS_CONFIG_CACHE, getCacheKey(config.getConfigKey()));
        }
    }

    @Override
    public void loadingConfigCache() {
        List<SysConfig> configs = configMapper.selectConfigList(new SysConfig());
        for (SysConfig config : configs) {
            CacheUtils.put(Constants.SYS_CONFIG_CACHE, getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
    }

    @Override
    public void clearConfigCache() {
        CacheUtils.removeAll(Constants.SYS_CONFIG_CACHE);
    }

    @Override
    public void resetConfigCache() {
        clearConfigCache();
        loadingConfigCache();
    }

    @Override
    public boolean checkConfigKeyUnique(SysConfig config) {
        String configId = StringUtils.defaultString(config.getId());
        SysConfig info = configMapper.checkConfigKeyUnique(config.getConfigKey());
        if (StringUtils.isNull(info)) {
            return UserConstants.UNIQUE;
        }
        return configId.equals(info.getId()) ? UserConstants.UNIQUE : UserConstants.NOT_UNIQUE;
    }

    /**
     * 构建缓存键
     */
    private String getCacheKey(String configKey) {
        return Constants.SYS_CONFIG_KEY + configKey;
    }
}
