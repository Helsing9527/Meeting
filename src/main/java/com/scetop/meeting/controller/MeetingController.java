package com.scetop.meeting.controller;

import com.scetop.meeting.controller.util.R;
import com.scetop.meeting.pojo.User;
import com.scetop.meeting.server.IUserServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/meeting")
public class MeetingController {
    @Autowired
    private IUserServer userServer;

    // 会议申请列表
    @GetMapping
    public R queryAll() {
        List<User> userList = userServer.list();
        return new R(true, userList, null);
    }
}