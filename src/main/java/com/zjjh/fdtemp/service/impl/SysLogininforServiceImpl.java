package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.beans.entity.SysLogininfor;
import com.zjjh.fdtemp.common.core.text.Convert;
import com.zjjh.fdtemp.dao.SysLogininforDao;
import com.zjjh.fdtemp.service.SysLogininforService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统访问日志情况信息 服务层处理
 *
 * @author szx
 */
@Service
public class SysLogininforServiceImpl implements SysLogininforService {

    private final SysLogininforDao logininforMapper;

    public SysLogininforServiceImpl(SysLogininforDao logininforMapper) {
        this.logininforMapper = logininforMapper;
    }

    @Override
    public void insertLogininfor(SysLogininfor logininfor) {
        logininforMapper.insertLogininfor(logininfor);
    }

    @Override
    public List<SysLogininfor> selectLogininforList(SysLogininfor logininfor) {
        return logininforMapper.selectLogininforList(logininfor);
    }

    @Override
    public int deleteLogininforByIds(String ids) {
        return logininforMapper.deleteLogininforByIds(Convert.toStrArray(ids));
    }

    @Override
    public void cleanLogininfor() {
        logininforMapper.cleanLogininfor();
    }
}
