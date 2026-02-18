package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.beans.entity.SysOperLog;
import com.zjjh.fdtemp.common.core.text.Convert;
import com.zjjh.fdtemp.dao.SysOperLogDao;
import com.zjjh.fdtemp.service.SysOperLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 操作日志 服务层处理
 *
 * @author szx
 */
@Service
@RequiredArgsConstructor
public class SysOperLogServiceImpl implements SysOperLogService {

    private final SysOperLogDao operLogMapper;

    @Override
    public void insertOperlog(SysOperLog operLog) {
        operLogMapper.insertOperlog(operLog);
    }

    @Override
    public List<SysOperLog> selectOperLogList(SysOperLog operLog) {
        return operLogMapper.selectOperLogList(operLog);
    }

    @Override
    public int deleteOperLogByIds(String ids) {
        return operLogMapper.deleteOperLogByIds(Convert.toStrArray(ids));
    }

    @Override
    public SysOperLog selectOperLogById(String operId) {
        return operLogMapper.selectOperLogById(operId);
    }

    @Override
    public void cleanOperLog() {
        operLogMapper.cleanOperLog();
    }
}
