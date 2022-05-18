package com.scetop.meeting.controller;

import com.scetop.meeting.controller.util.R;
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
        return new R(flag,null,flag ? "注册成功^_^" : "注册失败-_-!");
    }

    @PostMapping("/login")
    public R loginUser(@RequestBody String loginBase64) {
        System.out.println(loginBase64);
        if (loginBase64.length() > 0) {
            return new R(true,null, "服务器收到位图");
        }
        return new R(false, null, "服务器未收到位图");
    }
}
