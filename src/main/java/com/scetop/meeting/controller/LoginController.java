package com.scetop.meeting.controller;

import com.scetop.meeting.controller.util.R;
import com.scetop.meeting.pojo.Base64;
import com.scetop.meeting.pojo.User;
import com.scetop.meeting.server.IUserServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/index")
public class LoginController {

    @Autowired
    private IUserServer userServer;

    // 注册业务
    @PostMapping
    public R registerUser(@RequestBody User user) {
        System.out.println(user.getBase64());
        boolean flag = userServer.save(user);
        return new R(flag, null, flag ? "注册成功^_^" : "注册失败-_-!");
    }

    @PostMapping("/login")
    public R loginUser(@RequestBody Base64 loginBase64) {
        System.out.println(loginBase64.getLoginBase64());
        System.out.println(loginBase64.getLoginBase64().length() / 1024);
        return new R(true, null, "服务器收到位图");
    }
}
