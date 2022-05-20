package com.scetop.meeting.controller;

import com.scetop.meeting.controller.tencentapi.CreatePerson;
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

    @Autowired
    private CreatePerson createPerson;

    // 注册业务
    // 待解决bug:同一人重复注册问题/健壮性问题
    @PostMapping
    public R registerUser(@RequestBody User user) {
        // 先将用户信息存入数据库，MP回填id，再调用腾讯新增人员
        boolean save = userServer.save(user);
        if (save) {
            System.out.println("存入数据库成功");
            String faceId = createPerson.register(user);
            if (faceId != null) {
                System.out.println("存入腾讯人员库成功");
                Boolean saveFaceId = userServer.saveFaceId(faceId, user.getId());
                if (saveFaceId) {
                    return new R(true, null, "注册成功^_^");
                }
            }
        }
        return new R(false, null, "注册失败-_-!");
    }

    @PostMapping("/login")
    public R loginUser(@RequestBody Base64 loginBase64) {
        System.out.println(loginBase64.getLoginBase64());
        System.out.println(loginBase64.getLoginBase64().length() / 1024);
        return new R(true, null, "服务器收到位图");
    }
}
