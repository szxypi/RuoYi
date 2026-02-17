package com.zjjh.fdtemp.controller.quartz;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.zjjh.fdtemp.common.annotation.Log;
import com.zjjh.fdtemp.constants.Constants;
import com.zjjh.fdtemp.common.core.BaseController;
import com.zjjh.fdtemp.common.core.domain.AjaxResult;
import com.zjjh.fdtemp.common.core.page.TableDataInfo;
import com.zjjh.fdtemp.enums.BusinessType;
import com.zjjh.fdtemp.common.exception.job.TaskException;
import com.zjjh.fdtemp.common.utils.StringUtils;
import com.zjjh.fdtemp.common.utils.poi.ExcelUtil;
import com.zjjh.fdtemp.beans.entity.SysJob;
import com.zjjh.fdtemp.service.SysJobService;
import com.zjjh.fdtemp.common.utils.CronUtils;
import com.zjjh.fdtemp.common.utils.ScheduleUtils;

/**
 * 调度任务信息操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/job")
public class SysJobController extends BaseController
{
    @Autowired
    private SysJobService jobService;

    @PreAuthorize("hasAuthority('monitor:job:list')")
    @PostMapping("/list")
    public TableDataInfo list(SysJob job)
    {
        startPage();
        List<SysJob> list = jobService.selectJobList(job);
        return getDataTable(list);
    }

    @Log(title = "定时任务", businessType = BusinessType.EXPORT)
    @PreAuthorize("hasAuthority('monitor:job:export')")
    @PostMapping("/export")
    public AjaxResult export(SysJob job)
    {
        List<SysJob> list = jobService.selectJobList(job);
        ExcelUtil<SysJob> util = new ExcelUtil<SysJob>(SysJob.class);
        return util.exportExcel(list, "定时任务");
    }

    @Log(title = "定时任务", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('monitor:job:remove')")
    @PostMapping("/remove")
    public AjaxResult remove(String ids) throws SchedulerException
    {
        jobService.deleteJobByIds(ids);
        return success();
    }

    @PreAuthorize("hasAuthority('monitor:job:detail')")
    @GetMapping("/detail/{jobId}")
    public AjaxResult detail(@PathVariable("jobId") String jobId)
    {
        return success(jobService.selectJobById(jobId));
    }

    /**
     * 任务调度状态修改
     */
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('monitor:job:changeStatus')")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(SysJob job) throws SchedulerException
    {
        SysJob newJob = jobService.selectJobById(job.getId());
        newJob.setStatus(job.getStatus());
        return toAjax(jobService.changeStatus(newJob));
    }

    /**
     * 任务调度立即执行一次
     */
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('monitor:job:changeStatus')")
    @PostMapping("/run")
    public AjaxResult run(SysJob job) throws SchedulerException
    {
        boolean result = jobService.run(job);
        return result ? success() : error("任务不存在或已过期！");
    }

    /**
     * 新增保存调度
     */
    @Log(title = "定时任务", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('monitor:job:add')")
    @PostMapping("/add")
    public AjaxResult addSave(@Validated SysJob job) throws SchedulerException, TaskException
    {
        if (!CronUtils.isValid(job.getCronExpression()))
        {
            return error("新增任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        }
        else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI))
        {
            return error("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'rmi'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS }))
        {
            return error("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'ldap(s)'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.HTTP, Constants.HTTPS }))
        {
            return error("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'http(s)'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR))
        {
            return error("新增任务'" + job.getJobName() + "'失败，目标字符串存在违规");
        }
        else if (!ScheduleUtils.whiteList(job.getInvokeTarget()))
        {
            return error("新增任务'" + job.getJobName() + "'失败，目标字符串不在白名单内");
        }
        return toAjax(jobService.insertJob(job));
    }

    /**
     * 修改保存调度
     */
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('monitor:job:edit')")
    @PostMapping("/edit")
    public AjaxResult editSave(@Validated SysJob job) throws SchedulerException, TaskException
    {
        if (!CronUtils.isValid(job.getCronExpression()))
        {
            return error("修改任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        }
        else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI))
        {
            return error("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'rmi'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS }))
        {
            return error("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'ldap'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.HTTP, Constants.HTTPS }))
        {
            return error("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'http(s)'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR))
        {
            return error("修改任务'" + job.getJobName() + "'失败，目标字符串存在违规");
        }
        else if (!ScheduleUtils.whiteList(job.getInvokeTarget()))
        {
            return error("修改任务'" + job.getJobName() + "'失败，目标字符串不在白名单内");
        }
        return toAjax(jobService.updateJob(job));
    }

    /**
     * 校验cron表达式是否有效
     */
    @PostMapping("/checkCronExpressionIsValid")
    public boolean checkCronExpressionIsValid(SysJob job)
    {
        return jobService.checkCronExpressionIsValid(job.getCronExpression());
    }

    /**
     * 查询cron表达式近10次的执行时间
     */
    @GetMapping("/queryCronExpression")
    public AjaxResult queryCronExpression(@RequestParam(value = "cronExpression", required = false) String cronExpression)
    {
        if (jobService.checkCronExpressionIsValid(cronExpression))
        {
            List<String> dateList = CronUtils.getRecentTriggerTime(cronExpression);
            return success(dateList);
        }
        else
        {
            return error("表达式无效");
        }
    }
}
