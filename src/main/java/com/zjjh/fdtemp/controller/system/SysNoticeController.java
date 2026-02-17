package com.zjjh.fdtemp.controller.system;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zjjh.fdtemp.common.annotation.Log;
import com.zjjh.fdtemp.common.core.BaseController;
import com.zjjh.fdtemp.common.core.domain.AjaxResult;
import com.zjjh.fdtemp.common.core.page.TableDataInfo;
import com.zjjh.fdtemp.enums.BusinessType;
import com.zjjh.fdtemp.beans.entity.SysNotice;
import com.zjjh.fdtemp.service.SysNoticeService;

/**
 * 公告 信息操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/notice")
public class SysNoticeController extends BaseController
{
    @Autowired
    private SysNoticeService noticeService;

    /**
     * 查询公告列表
     */
    @PreAuthorize("hasAuthority('system:notice:list')")
    @PostMapping("/list")
    public TableDataInfo list(SysNotice notice)
    {
        startPage();
        List<SysNotice> list = noticeService.selectNoticeList(notice);
        return getDataTable(list);
    }

    /**
     * 新增保存公告
     */
    @PreAuthorize("hasAuthority('system:notice:add')")
    @Log(title = "通知公告", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult addSave(@Validated SysNotice notice)
    {
        return toAjax(noticeService.insertNotice(notice));
    }

    /**
     * 修改保存公告
     */
    @PreAuthorize("hasAuthority('system:notice:edit')")
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult editSave(@Validated SysNotice notice)
    {
        return toAjax(noticeService.updateNotice(notice));
    }

    /**
     * 删除公告
     */
    @PreAuthorize("hasAuthority('system:notice:remove')")
    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    public AjaxResult remove(String ids)
    {
        return toAjax(noticeService.deleteNoticeByIds(ids));
    }
}
