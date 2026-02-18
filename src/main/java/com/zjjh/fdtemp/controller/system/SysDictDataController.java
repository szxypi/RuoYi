package com.zjjh.fdtemp.controller.system;

import com.zjjh.fdtemp.beans.entity.SysDictData;
import com.zjjh.fdtemp.common.annotation.Log;
import com.zjjh.fdtemp.common.core.BaseController;
import com.zjjh.fdtemp.common.core.domain.AjaxResult;
import com.zjjh.fdtemp.common.core.page.TableDataInfo;
import com.zjjh.fdtemp.common.utils.poi.ExcelUtil;
import com.zjjh.fdtemp.enums.BusinessType;
import com.zjjh.fdtemp.service.SysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据字典信息
 *
 * @author szx
 */
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController extends BaseController {
    @Autowired
    private SysDictDataService dictDataService;

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('system:dict:list')")
    public TableDataInfo list(SysDictData dictData) {
        startPage();
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        return getDataTable(list);
    }

    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
    @PreAuthorize("hasAuthority('system:dict:export')")
    @PostMapping("/export")
    public AjaxResult export(SysDictData dictData) {
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        ExcelUtil<SysDictData> util = new ExcelUtil<SysDictData>(SysDictData.class);
        return util.exportExcel(list, "字典数据");
    }

    /**
     * 新增保存字典类型
     */
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:dict:add')")
    @PostMapping("/add")
    public AjaxResult addSave(@Validated SysDictData dict) {
        return toAjax(dictDataService.insertDictData(dict));
    }

    /**
     * 修改保存字典类型
     */
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:dict:edit')")
    @PostMapping("/edit")
    public AjaxResult editSave(@Validated SysDictData dict) {
        return toAjax(dictDataService.updateDictData(dict));
    }

    @Log(title = "字典数据", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:dict:remove')")
    @PostMapping("/remove")
    public AjaxResult remove(String ids) {
        dictDataService.deleteDictDataByIds(ids);
        return success();
    }
}
