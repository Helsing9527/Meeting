package com.scetop.meeting.controller;

import com.scetop.meeting.pojo.Staff;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PageController {
    // 登录页
    @RequestMapping
    public String loginPage() {
        return "login";
    }

    // 登录成功
    @RequestMapping("/workstation")
    public String workstation(HttpServletRequest request) {
        // 判断 session 中是否有登录用户，没有跳转登录页
        Staff staffInfo = (Staff) request.getSession().getAttribute("staffInfo");
        if (staffInfo != null) {
            return "workstation";
        }
        return "login";
    }

    // 签到页
    @RequestMapping("/checkIn")
    public String checkIn() {
        return "checkIn";
    }

    // 管理员注册页
    @RequestMapping("/init")
    public String setting() {
        return "init";
    }
}
