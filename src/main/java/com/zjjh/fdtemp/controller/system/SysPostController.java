package com.zjjh.fdtemp.controller.system;

import com.zjjh.fdtemp.beans.entity.SysPost;
import com.zjjh.fdtemp.common.annotation.Log;
import com.zjjh.fdtemp.common.core.BaseController;
import com.zjjh.fdtemp.common.core.domain.AjaxResult;
import com.zjjh.fdtemp.common.core.page.TableDataInfo;
import com.zjjh.fdtemp.common.utils.poi.ExcelUtil;
import com.zjjh.fdtemp.enums.BusinessType;
import com.zjjh.fdtemp.service.SysPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 岗位信息操作处理
 *
 * @author szx
 */
@RestController
@RequestMapping("/system/post")
public class SysPostController extends BaseController {
    @Autowired
    private SysPostService postService;

    @PreAuthorize("hasAuthority('system:post:list')")
    @PostMapping("/list")
    public TableDataInfo list(SysPost post) {
        startPage();
        List<SysPost> list = postService.selectPostList(post);
        return getDataTable(list);
    }

    @Log(title = "岗位管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("hasAuthority('system:post:export')")
    @PostMapping("/export")
    public AjaxResult export(SysPost post) {
        List<SysPost> list = postService.selectPostList(post);
        ExcelUtil<SysPost> util = new ExcelUtil<SysPost>(SysPost.class);
        return util.exportExcel(list, "岗位数据");
    }

    @PreAuthorize("hasAuthority('system:post:remove')")
    @Log(title = "岗位管理", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    public AjaxResult remove(String ids) {
        return toAjax(postService.deletePostByIds(ids));
    }

    /**
     * 新增保存岗位
     */
    @PreAuthorize("hasAuthority('system:post:add')")
    @Log(title = "岗位管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult addSave(@Validated SysPost post) {
        if (!postService.checkPostNameUnique(post)) {
            return error("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        } else if (!postService.checkPostCodeUnique(post)) {
            return error("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        return toAjax(postService.insertPost(post));
    }

    /**
     * 修改保存岗位
     */
    @PreAuthorize("hasAuthority('system:post:edit')")
    @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult editSave(@Validated SysPost post) {
        if (!postService.checkPostNameUnique(post)) {
            return error("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        } else if (!postService.checkPostCodeUnique(post)) {
            return error("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        return toAjax(postService.updatePost(post));
    }

    /**
     * 校验岗位名称
     */
    @PostMapping("/checkPostNameUnique")
    public boolean checkPostNameUnique(SysPost post) {
        return postService.checkPostNameUnique(post);
    }

    /**
     * 校验岗位编码
     */
    @PostMapping("/checkPostCodeUnique")
    public boolean checkPostCodeUnique(SysPost post) {
        return postService.checkPostCodeUnique(post);
    }
}
