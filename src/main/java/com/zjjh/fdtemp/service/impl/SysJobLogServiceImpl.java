package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.beans.entity.SysJobLog;
import com.zjjh.fdtemp.common.core.text.Convert;
import com.zjjh.fdtemp.dao.SysJobLogDao;
import com.zjjh.fdtemp.service.SysJobLogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 定时任务调度日志信息 服务层
 *
 * @author szx
 */
@Service
public class SysJobLogServiceImpl implements SysJobLogService {

    private final SysJobLogDao jobLogMapper;

    public SysJobLogServiceImpl(SysJobLogDao jobLogMapper) {
        this.jobLogMapper = jobLogMapper;
    }

    @Override
    public List<SysJobLog> selectJobLogList(SysJobLog jobLog) {
        return jobLogMapper.selectJobLogList(jobLog);
    }

    @Override
    public SysJobLog selectJobLogById(String jobLogId) {
        return jobLogMapper.selectJobLogById(jobLogId);
    }

    @Override
    public void addJobLog(SysJobLog jobLog) {
        jobLogMapper.insertJobLog(jobLog);
    }

    @Override
    public int deleteJobLogByIds(String ids) {
        return jobLogMapper.deleteJobLogByIds(Convert.toStrArray(ids));
    }

    @Override
    public int deleteJobLogById(String jobId) {
        return jobLogMapper.deleteJobLogById(jobId);
    }

    @Override
    public void cleanJobLog() {
        jobLogMapper.cleanJobLog();
    }
}
