package com.zjjh.fdtemp.controller.quartz;

import com.zjjh.fdtemp.beans.entity.SysJobLog;
import com.zjjh.fdtemp.common.annotation.Log;
import com.zjjh.fdtemp.common.core.BaseController;
import com.zjjh.fdtemp.common.core.domain.AjaxResult;
import com.zjjh.fdtemp.common.core.page.TableDataInfo;
import com.zjjh.fdtemp.common.utils.poi.ExcelUtil;
import com.zjjh.fdtemp.enums.BusinessType;
import com.zjjh.fdtemp.service.SysJobLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 调度日志操作处理
 *
 * @author szx
 */
@RestController
@RequestMapping("/monitor/jobLog")
public class SysJobLogController extends BaseController {
    @Autowired
    private SysJobLogService jobLogService;

    @PreAuthorize("hasAuthority('monitor:job:list')")
    @PostMapping("/list")
    public TableDataInfo list(SysJobLog jobLog) {
        startPage();
        List<SysJobLog> list = jobLogService.selectJobLogList(jobLog);
        return getDataTable(list);
    }

    @Log(title = "调度日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("hasAuthority('monitor:job:export')")
    @PostMapping("/export")
    public AjaxResult export(SysJobLog jobLog) {
        List<SysJobLog> list = jobLogService.selectJobLogList(jobLog);
        ExcelUtil<SysJobLog> util = new ExcelUtil<SysJobLog>(SysJobLog.class);
        return util.exportExcel(list, "调度日志");
    }

    @Log(title = "调度日志", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('monitor:job:remove')")
    @PostMapping("/remove")
    public AjaxResult remove(String ids) {
        return toAjax(jobLogService.deleteJobLogByIds(ids));
    }

    @PreAuthorize("hasAuthority('monitor:job:detail')")
    @GetMapping("/detail/{jobLogId}")
    public AjaxResult detail(@PathVariable("jobLogId") String jobLogId) {
        return success(jobLogService.selectJobLogById(jobLogId));
    }

    @Log(title = "调度日志", businessType = BusinessType.CLEAN)
    @PreAuthorize("hasAuthority('monitor:job:remove')")
    @PostMapping("/clean")
    public AjaxResult clean() {
        jobLogService.cleanJobLog();
        return success();
    }
}
