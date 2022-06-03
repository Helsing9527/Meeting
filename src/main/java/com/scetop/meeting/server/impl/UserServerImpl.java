package com.scetop.meeting.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scetop.meeting.mapper.UserMapper;
import com.scetop.meeting.pojo.User;
import com.scetop.meeting.server.IUserServer;
import org.apache.logging.log4j.util.Strings;
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
    public IPage<User> getPage(Integer currentPage, Integer pageSize, User user) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(Strings.isNotEmpty(user.getName()), User::getName, user.getName());
        lambdaQueryWrapper.like(Strings.isNotEmpty(user.getDept()), User::getDept, user.getDept());
        lambdaQueryWrapper.like(Strings.isNotEmpty(user.getPost()), User::getPost, user.getPost());
        IPage<User> page = new Page<>(currentPage, pageSize);
        userMapper.selectPage(page,lambdaQueryWrapper);
        return page;
    }

    @Override
    public Boolean saveAdminCode(String adminCode, Integer id) {
        return userMapper.saveAdminCode(adminCode, id);
    }
}
