package com.scetop.meeting.server.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scetop.meeting.mapper.UserMapper;
import com.scetop.meeting.pojo.User;
import com.scetop.meeting.server.IUserServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServerImpl extends ServiceImpl<UserMapper, User> implements IUserServer {
    @Autowired
    private UserMapper userMapper;

    @Override
    public Boolean saveFaceId(String faceId, Integer id) {
        return userMapper.saveFaceId(faceId, id);
    }

    @Override
    public IPage<User> getPage(Integer currentPage, Integer pageSize) {
        IPage<User> page = new Page<>(currentPage, pageSize);
        userMapper.selectPage(page,null);
        return page;
    }
}
