package com.scetop.meeting.controller;

import com.alibaba.fastjson.JSONObject;
import com.scetop.meeting.common.R;
import com.scetop.meeting.compreFace.RecognizeFace;
import com.scetop.meeting.pojo.Staff;
import com.scetop.meeting.service.IStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private RecognizeFace recognizeFace;

    @Autowired
    private IStaffService staffService;

    // 用户登录
    @PostMapping
    public R userLogin(@RequestBody Staff staff, HttpServletRequest request) {
        // 判断上传图像是否大于10k
        if (staff.getImgBase64().length() / 1024 >= 10) {
            // 返回人脸库中的人员名称（staffId_staffUserName)
            String staffUserName = recognizeFace.recognizeFace(staff.getImgBase64());
            if (staffUserName != null) {
                // 正则表达式获取 staffId
                String regEx = "[^0-9]";
                Pattern pattern = Pattern.compile(regEx);
                Matcher matcher = pattern.matcher(staffUserName);
                String trim = matcher.replaceAll("").trim();
                Staff staffInfo = staffService.getById(trim);
                String loginStaff = JSONObject.toJSONString(staffInfo);
                // 登录成功存入 session 浏览器存入 localStorage
                request.getSession().setAttribute("staffInfo", staffInfo);
                return new R(true, loginStaff, "登录成功，即将跳转");
            }
            return new R(false, null, "登录失败，环境过暗或过亮！并请确认为注册用户");
        }
        return new R(false, null, "登录失败，未检测到人脸，请重新刷脸登录 -_-||");
    }
}
