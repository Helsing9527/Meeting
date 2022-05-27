package com.scetop.meeting.controller;

import com.scetop.meeting.tencentapi.person.CreatePerson;
import com.scetop.meeting.tencentapi.person.GetPersonList;
import com.scetop.meeting.tencentapi.face.VerifyFace;
import com.scetop.meeting.controller.util.R;
import com.scetop.meeting.pojo.Base64;
import com.scetop.meeting.pojo.User;
import com.scetop.meeting.server.IUserServer;
import com.tencentcloudapi.iai.v20200303.models.CreatePersonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping(value = "/index")
public class IndexController {
    @Autowired
    private Environment env;

    @Autowired
    private IUserServer userServer;

    @Autowired
    private CreatePerson createPerson;

    @Autowired
    private GetPersonList getPersonList;

    @Autowired
    private VerifyFace verifyFace;

    // 注册业务
    @PostMapping
    public R registerUser(@RequestBody User user) {
        // 先将用户信息存入数据库，MP回填id，再调用腾讯新增人员
        boolean save = userServer.save(user);
        if (save) {
            // 上传腾讯云人员库
//            String faceId = createPerson.register(user);
            CreatePersonResponse register = createPerson.register(user);
            if (register.getFaceId() != null && register.getSimilarPersonId() == null) {
                // 存储返回的人脸信息
                Boolean saveFaceId = userServer.saveFaceId(register.getFaceId(), user.getId());
                // 判断是否为管理员注册
                if ((env.getProperty("adminCode").equals(user.getAdminCode()) || "admin".equals(user.getAdminCode())) && saveFaceId) {
                    Boolean saveAdminCode = userServer.saveAdminCode("admin", user.getId());
                    if (saveAdminCode) {
                        return new R(true, null, "管理员注册成功^_^");
                    }
                } else if (saveFaceId) {
                    return new R(true, null, "注册成功^_^");
                }
            }
            userServer.removeById(user.getId());
            return new R(false, null, "照片异常 注册失败-_-!");
        }
        return new R(false, null, "注册失败-_-!");
    }

    // 人脸识别登录
    @PostMapping("/login")
    public R loginUser(@RequestBody Base64 loginBase64, HttpServletRequest request) {
        HttpSession session = request.getSession();
        // 校验图片大小大于10k，小于10k返回登录失败
        if (loginBase64.getLoginBase64().length() / 1024 >= 10) {
            int userId = 0;
            // 获取人员库所有人员id
            List<String> personList = getPersonList.getPersonList();
            // 遍历调用验证人员
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
                session.setAttribute("user", user);
                return new R(true, user, "登录成功，即将跳转 ^_^");
            }
            return new R(false, null, "未注册，请注册后再登录 -_-||");
        }
        return new R(false, null, "登录失败，图片异常，请重新登录 -_-||");
    }
}
