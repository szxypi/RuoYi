package com.zjjh.fdtemp.controller.system;

import com.zjjh.fdtemp.beans.entity.SysDictType;
import com.zjjh.fdtemp.common.annotation.Log;
import com.zjjh.fdtemp.common.core.BaseController;
import com.zjjh.fdtemp.common.core.domain.AjaxResult;
import com.zjjh.fdtemp.common.core.domain.Ztree;
import com.zjjh.fdtemp.common.core.page.TableDataInfo;
import com.zjjh.fdtemp.common.utils.poi.ExcelUtil;
import com.zjjh.fdtemp.enums.BusinessType;
import com.zjjh.fdtemp.service.SysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/system/dict")
public class SysDictTypeController extends BaseController {
    @Autowired
    private SysDictTypeService dictTypeService;

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('system:dict:list')")
    public TableDataInfo list(SysDictType dictType) {
        startPage();
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        return getDataTable(list);
    }

    @Log(title = "字典类型", businessType = BusinessType.EXPORT)
    @PreAuthorize("hasAuthority('system:dict:export')")
    @PostMapping("/export")
    public AjaxResult export(SysDictType dictType) {

        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        ExcelUtil<SysDictType> util = new ExcelUtil<SysDictType>(SysDictType.class);
        return util.exportExcel(list, "字典类型");
    }

    /**
     * 新增保存字典类型
     */
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:dict:add')")
    @PostMapping("/add")
    public AjaxResult addSave(@Validated SysDictType dict) {
        if (!dictTypeService.checkDictTypeUnique(dict)) {
            return error("新增字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        return toAjax(dictTypeService.insertDictType(dict));
    }

    /**
     * 修改保存字典类型
     */
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:dict:edit')")
    @PostMapping("/edit")
    public AjaxResult editSave(@Validated SysDictType dict) {
        if (!dictTypeService.checkDictTypeUnique(dict)) {
            return error("修改字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        return toAjax(dictTypeService.updateDictType(dict));
    }

    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:dict:remove')")
    @PostMapping("/remove")
    public AjaxResult remove(String ids) {
        dictTypeService.deleteDictTypeByIds(ids);
        return success();
    }

    /**
     * 刷新字典缓存
     */
    @PreAuthorize("hasAuthority('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.CLEAN)
    @GetMapping("/refreshCache")
    public AjaxResult refreshCache() {
        dictTypeService.resetDictCache();
        return success();
    }

    /**
     * 校验字典类型
     */
    @PostMapping("/checkDictTypeUnique")
    public boolean checkDictTypeUnique(SysDictType dictType) {
        return dictTypeService.checkDictTypeUnique(dictType);
    }

    /**
     * 加载字典列表树
     */
    @GetMapping("/treeData")
    public List<Ztree> treeData() {
        List<Ztree> ztrees = dictTypeService.selectDictTree(new SysDictType());
        return ztrees;
    }
}
