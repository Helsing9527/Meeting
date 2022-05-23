package com.scetop.meeting.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scetop.meeting.controller.util.R;
import com.scetop.meeting.pojo.User;
import com.scetop.meeting.server.IUserServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/meeting")
public class MeetingController {
    @Autowired
    private IUserServer userServer;

    // 会议申请列表
    @GetMapping("/{currentPage}/{pageSize}")
    public R queryAll(@PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        IPage<User> page = userServer.getPage(currentPage, pageSize);
        return new R(true, page, null);
    }

    // 登录个人信息回填
    @GetMapping("/userInfo/{id}")
    public R queryById(@PathVariable String id) {
        System.out.println(id);
        User user = userServer.getById(id);
        System.out.println(user);
        return new R(true, user, null);
    }
}
