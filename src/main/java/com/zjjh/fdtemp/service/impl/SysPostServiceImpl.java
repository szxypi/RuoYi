package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.beans.entity.SysPost;
import com.zjjh.fdtemp.common.core.text.Convert;
import com.zjjh.fdtemp.common.exception.ServiceException;
import com.zjjh.fdtemp.common.utils.StringUtils;
import com.zjjh.fdtemp.constants.UserConstants;
import com.zjjh.fdtemp.dao.SysPostDao;
import com.zjjh.fdtemp.dao.SysUserPostDao;
import com.zjjh.fdtemp.service.SysPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 岗位信息 服务层处理
 *
 * @author szx
 */
@Service
public class SysPostServiceImpl implements SysPostService {
    @Autowired
    private SysPostDao postMapper;

    @Autowired
    private SysUserPostDao userPostMapper;

    @Override
    public List<SysPost> selectPostList(SysPost post) {
        return postMapper.selectPostList(post);
    }

    @Override
    public List<SysPost> selectPostAll() {
        return postMapper.selectPostAll();
    }

    @Override
    public List<SysPost> selectPostsByUserId(String userId) {
        Set<String> userPostIds = postMapper.selectPostsByUserId(userId)
                .stream()
                .map(SysPost::getId)
                .collect(Collectors.toSet());

        List<SysPost> posts = postMapper.selectPostAll();
        posts.stream()
                .filter(post -> userPostIds.contains(post.getId()))
                .forEach(post -> post.setFlag(true));
        return posts;
    }

    @Override
    public SysPost selectPostById(String postId) {
        return postMapper.selectPostById(postId);
    }

    @Override
    public int deletePostByIds(String ids) {
        String[] postIds = Convert.toStrArray(ids);
        for (String postId : postIds) {
            if (countUserPostById(postId) > 0) {
                SysPost post = selectPostById(postId);
                throw new ServiceException(String.format("%s已分配,不能删除", post.getPostName()));
            }
        }
        return postMapper.deletePostByIds(postIds);
    }

    @Override
    public int insertPost(SysPost post) {
        return postMapper.insertPost(post);
    }

    @Override
    public int updatePost(SysPost post) {
        return postMapper.updatePost(post);
    }

    @Override
    public int countUserPostById(String postId) {
        return userPostMapper.countUserPostById(postId);
    }

    @Override
    public boolean checkPostNameUnique(SysPost post) {
        SysPost info = postMapper.checkPostNameUnique(post.getPostName());
        return isUnique(post.getId(), info);
    }

    @Override
    public boolean checkPostCodeUnique(SysPost post) {
        SysPost info = postMapper.checkPostCodeUnique(post.getPostCode());
        return isUnique(post.getId(), info);
    }

    /**
     * 判断是否唯一
     */
    private boolean isUnique(String currentId, SysPost existing) {
        String postId = StringUtils.isNull(currentId) ? "" : currentId;
        return StringUtils.isNull(existing) || existing.getId().equals(postId)
                ? UserConstants.UNIQUE
                : UserConstants.NOT_UNIQUE;
    }
}
