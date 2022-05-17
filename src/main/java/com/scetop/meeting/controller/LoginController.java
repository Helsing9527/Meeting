package com.scetop.meeting.controller;

import com.scetop.meeting.controller.util.R;
import com.scetop.meeting.face.FaceService;
import com.scetop.meeting.pojo.User;
import com.scetop.meeting.server.IUserServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private IUserServer userServer;

    // 注册业务
    @PostMapping
    public R login(@RequestBody User user) {
//        String hex = FaceService.add(image, "1", "hex");
        boolean flag = userServer.save(user);
        return new R(flag,null,null);
    }

    @GetMapping
    public R queryAll() {
        List<User> userList = userServer.list();
        return new R(true, userList, null);
    }
}
