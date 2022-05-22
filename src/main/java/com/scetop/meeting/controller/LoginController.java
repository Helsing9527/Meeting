package com.scetop.meeting.controller;

import com.scetop.meeting.controller.Contants.Contants;
import com.scetop.meeting.tencentapi.CreatePerson;
import com.scetop.meeting.tencentapi.GetPersonList;
import com.scetop.meeting.tencentapi.VerifyFace;
import com.scetop.meeting.controller.util.R;
import com.scetop.meeting.pojo.Base64;
import com.scetop.meeting.pojo.User;
import com.scetop.meeting.server.IUserServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping(value = "/index")
public class LoginController {

    @Autowired
    private IUserServer userServer;

    @Autowired
    private CreatePerson createPerson;

    @Autowired
    private GetPersonList getPersonList;

    @Autowired
    private VerifyFace verifyFace;

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

    // 人脸识别登录
    @PostMapping("/login")
    public R loginUser(@RequestBody Base64 loginBase64,HttpSession session) {
        // 校验图片大小大于10k，小于10k返回登录失败
        if (loginBase64.getLoginBase64().length() / 1024 >= 10) {
            int userId = 0;
            // 获取人员库所有人员id
            List<String> personList = getPersonList.getPersonList();
            // 便利调用验证人员
            for (String personId : personList) {
                Boolean flag = verifyFace.verifyFace(loginBase64.getLoginBase64(), personId);
                // 匹配人员返回人员id
                if (flag) {
                    userId = Integer.parseInt(personId);
                    break;
                }
            }
            // 人员库内匹配登录人员，登录成功
            if (userId != 0) {
                User user = userServer.getById(userId);
                session.setAttribute("Contants", Contants.user_info);
                return new R(true, user, "登录成功，即将跳转 ^_^");
            }
//            System.out.println(userId);
            return new R(false, null, "登录失败，请重新登录 -_-||");
        }
        return new R(false, null, "登录失败，图片异常，请重新登录 -_-||");
    }
}
