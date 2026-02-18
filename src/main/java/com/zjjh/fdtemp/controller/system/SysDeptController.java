package com.zjjh.fdtemp.controller.system;

import com.zjjh.fdtemp.beans.entity.SysDept;
import com.zjjh.fdtemp.common.annotation.Log;
import com.zjjh.fdtemp.common.core.BaseController;
import com.zjjh.fdtemp.common.core.domain.AjaxResult;
import com.zjjh.fdtemp.common.core.domain.Ztree;
import com.zjjh.fdtemp.common.utils.StringUtils;
import com.zjjh.fdtemp.constants.UserConstants;
import com.zjjh.fdtemp.enums.BusinessType;
import com.zjjh.fdtemp.service.SysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门信息
 *
 * @author szx
 */
@RestController
@RequestMapping("/system/dept")
public class SysDeptController extends BaseController {
    @Autowired
    private SysDeptService deptService;

    @PreAuthorize("hasAuthority('system:dept:list')")
    @GetMapping("/list")
    public List<SysDept> list(SysDept dept) {
        List<SysDept> deptList = deptService.selectDeptList(dept);
        return deptList;
    }

    /**
     * 新增保存部门
     */
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:dept:add')")
    @PostMapping("/add")
    public AjaxResult addSave(@Validated SysDept dept) {
        if (!deptService.checkDeptNameUnique(dept)) {
            return error("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        return toAjax(deptService.insertDept(dept));
    }

    /**
     * 修改保存部门
     */
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:dept:edit')")
    @PostMapping("/edit")
    public AjaxResult editSave(@Validated SysDept dept) {
        String deptId = dept.getId();
        deptService.checkDeptDataScope(deptId);
        if (!deptService.checkDeptNameUnique(dept)) {
            return error("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        } else if (dept.getParentId().equals(deptId)) {
            return error("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        } else if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.getStatus()) && deptService.selectNormalChildrenDeptById(deptId) > 0) {
            return AjaxResult.error("该部门包含未停用的子部门！");
        }
        return toAjax(deptService.updateDept(dept));
    }

    /**
     * 删除
     */
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:dept:remove')")
    @DeleteMapping("/{deptId}")
    public AjaxResult remove(@PathVariable("deptId") String deptId) {
        if (deptService.selectDeptCount(deptId) > 0) {
            return AjaxResult.warn("存在下级部门,不允许删除");
        }
        if (deptService.checkDeptExistUser(deptId)) {
            return AjaxResult.warn("部门存在用户,不允许删除");
        }
        deptService.checkDeptDataScope(deptId);
        return toAjax(deptService.deleteDeptById(deptId));
    }

    /**
     * 校验部门名称
     */
    @PostMapping("/checkDeptNameUnique")
    public boolean checkDeptNameUnique(SysDept dept) {
        return deptService.checkDeptNameUnique(dept);
    }

    /**
     * 加载部门列表树（排除下级）
     */
    @PreAuthorize("hasAuthority('system:dept:list')")
    @GetMapping("/treeData/{excludeId}")
    public List<Ztree> treeDataExcludeChild(@PathVariable(value = "excludeId", required = false) String excludeId) {
        SysDept dept = new SysDept();
        dept.setExcludeId(excludeId);
        List<Ztree> ztrees = deptService.selectDeptTreeExcludeChild(dept);
        return ztrees;
    }
}
