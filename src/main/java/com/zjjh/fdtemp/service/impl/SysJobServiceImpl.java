package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.beans.entity.SysJob;
import com.zjjh.fdtemp.common.core.text.Convert;
import com.zjjh.fdtemp.common.exception.job.TaskException;
import com.zjjh.fdtemp.common.utils.CronUtils;
import com.zjjh.fdtemp.common.utils.ScheduleUtils;
import com.zjjh.fdtemp.constants.ScheduleConstants;
import com.zjjh.fdtemp.dao.SysJobDao;
import com.zjjh.fdtemp.service.SysJobService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 定时任务调度信息 服务层
 *
 * @author szx
 */
@Service
@RequiredArgsConstructor
public class SysJobServiceImpl implements SysJobService {

    private final Scheduler scheduler;
    private final SysJobDao jobMapper;

    /**
     * 项目启动时，初始化定时器
     * 主要是防止手动修改数据库导致未同步到定时任务处理
     * 注：不能手动修改数据库ID和任务组名，否则会导致脏数据
     */
    @PostConstruct
    public void init() throws SchedulerException, TaskException {
        scheduler.clear();
        List<SysJob> jobList = jobMapper.selectJobAll();
        for (SysJob job : jobList) {
            ScheduleUtils.createScheduleJob(scheduler, job);
        }
    }

    @Override
    public List<SysJob> selectJobList(SysJob job) {
        return jobMapper.selectJobList(job);
    }

    @Override
    public SysJob selectJobById(String jobId) {
        return jobMapper.selectJobById(jobId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int pauseJob(SysJob job) throws SchedulerException {
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        int rows = jobMapper.updateJob(job);
        if (rows > 0) {
            scheduler.pauseJob(ScheduleUtils.getJobKey(job.getId(), job.getJobGroup()));
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int resumeJob(SysJob job) throws SchedulerException {
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        int rows = jobMapper.updateJob(job);
        if (rows > 0) {
            scheduler.resumeJob(ScheduleUtils.getJobKey(job.getId(), job.getJobGroup()));
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteJob(SysJob job) throws SchedulerException {
        int rows = jobMapper.deleteJobById(job.getId());
        if (rows > 0) {
            scheduler.deleteJob(ScheduleUtils.getJobKey(job.getId(), job.getJobGroup()));
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobByIds(String ids) throws SchedulerException {
        for (String jobId : Convert.toStrArray(ids)) {
            deleteJob(jobMapper.selectJobById(jobId));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(SysJob job) throws SchedulerException {
        String status = job.getStatus();
        if (ScheduleConstants.Status.NORMAL.getValue().equals(status)) {
            return resumeJob(job);
        }
        if (ScheduleConstants.Status.PAUSE.getValue().equals(status)) {
            return pauseJob(job);
        }
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean run(SysJob job) throws SchedulerException {
        SysJob tmpObj = selectJobById(job.getId());
        JobKey jobKey = ScheduleUtils.getJobKey(job.getId(), tmpObj.getJobGroup());
        if (!scheduler.checkExists(jobKey)) {
            return false;
        }
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(ScheduleConstants.TASK_PROPERTIES, tmpObj);
        scheduler.triggerJob(jobKey, dataMap);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertJob(SysJob job) throws SchedulerException, TaskException {
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        int rows = jobMapper.insertJob(job);
        if (rows > 0) {
            ScheduleUtils.createScheduleJob(scheduler, job);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateJob(SysJob job) throws SchedulerException, TaskException {
        SysJob properties = selectJobById(job.getId());
        int rows = jobMapper.updateJob(job);
        if (rows > 0) {
            updateSchedulerJob(job, properties.getJobGroup());
        }
        return rows;
    }

    /**
     * 更新任务
     *
     * @param job      任务对象
     * @param jobGroup 任务组名
     */
    private void updateSchedulerJob(SysJob job, String jobGroup) throws SchedulerException, TaskException {
        JobKey jobKey = ScheduleUtils.getJobKey(job.getId(), jobGroup);
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }
        ScheduleUtils.createScheduleJob(scheduler, job);
    }

    @Override
    public boolean checkCronExpressionIsValid(String cronExpression) {
        return CronUtils.isValid(cronExpression);
    }
}
