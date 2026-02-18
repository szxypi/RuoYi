package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.beans.entity.SysNotice;
import com.zjjh.fdtemp.common.core.text.Convert;
import com.zjjh.fdtemp.dao.SysNoticeDao;
import com.zjjh.fdtemp.service.SysNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公告 服务层实现
 *
 * @author szx
 */
@Service
@RequiredArgsConstructor
public class SysNoticeServiceImpl implements SysNoticeService {

    private final SysNoticeDao noticeMapper;

    @Override
    public SysNotice selectNoticeById(String noticeId) {
        return noticeMapper.selectNoticeById(noticeId);
    }

    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice) {
        return noticeMapper.selectNoticeList(notice);
    }

    @Override
    public int insertNotice(SysNotice notice) {
        return noticeMapper.insertNotice(notice);
    }

    @Override
    public int updateNotice(SysNotice notice) {
        return noticeMapper.updateNotice(notice);
    }

    @Override
    public int deleteNoticeByIds(String ids) {
        return noticeMapper.deleteNoticeByIds(Convert.toStrArray(ids));
    }
}
