package com.zjjh.fdtemp.controller.system;

import com.zjjh.fdtemp.beans.entity.SysConfig;
import com.zjjh.fdtemp.common.annotation.Log;
import com.zjjh.fdtemp.common.core.BaseController;
import com.zjjh.fdtemp.common.core.domain.AjaxResult;
import com.zjjh.fdtemp.common.core.page.TableDataInfo;
import com.zjjh.fdtemp.common.utils.poi.ExcelUtil;
import com.zjjh.fdtemp.enums.BusinessType;
import com.zjjh.fdtemp.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 参数配置 信息操作处理
 *
 * @author szx
 */
@RestController
@RequestMapping("/system/config")
public class SysConfigController extends BaseController {
    @Autowired
    private SysConfigService configService;

    /**
     * 查询参数配置列表
     */
    @PreAuthorize("hasAuthority('system:config:list')")
    @PostMapping("/list")
    public TableDataInfo list(SysConfig config) {
        startPage();
        List<SysConfig> list = configService.selectConfigList(config);
        return getDataTable(list);
    }

    @Log(title = "参数管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("hasAuthority('system:config:export')")
    @PostMapping("/export")
    public AjaxResult export(SysConfig config) {
        List<SysConfig> list = configService.selectConfigList(config);
        ExcelUtil<SysConfig> util = new ExcelUtil<SysConfig>(SysConfig.class);
        return util.exportExcel(list, "参数数据");
    }

    /**
     * 新增保存参数配置
     */
    @PreAuthorize("hasAuthority('system:config:add')")
    @Log(title = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult addSave(@Validated SysConfig config) {
        if (!configService.checkConfigKeyUnique(config)) {
            return error("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        return toAjax(configService.insertConfig(config));
    }

    /**
     * 修改保存参数配置
     */
    @PreAuthorize("hasAuthority('system:config:edit')")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult editSave(@Validated SysConfig config) {
        if (!configService.checkConfigKeyUnique(config)) {
            return error("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        return toAjax(configService.updateConfig(config));
    }

    /**
     * 删除参数配置
     */
    @PreAuthorize("hasAuthority('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    public AjaxResult remove(String ids) {
        configService.deleteConfigByIds(ids);
        return success();
    }

    /**
     * 刷新参数缓存
     */
    @PreAuthorize("hasAuthority('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.CLEAN)
    @GetMapping("/refreshCache")
    public AjaxResult refreshCache() {
        configService.resetConfigCache();
        return success();
    }

    /**
     * 校验参数键名
     */
    @PostMapping("/checkConfigKeyUnique")
    public boolean checkConfigKeyUnique(SysConfig config) {
        return configService.checkConfigKeyUnique(config);
    }
}
