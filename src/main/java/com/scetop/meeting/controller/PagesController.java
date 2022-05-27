package com.scetop.meeting.controller;

import com.scetop.meeting.pojo.User;
import com.scetop.meeting.server.IUserServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PagesController {

    @Autowired
    private IUserServer userServer;

    // 跳转登录成功页
    @RequestMapping("/home/{id}")
    public ModelAndView home(@PathVariable(name = "id") String id) {
        User user = userServer.getById(id);
        ModelAndView modelAndView = new ModelAndView();
        // 健壮性判断，防止空指针
        if (user != null) {
            // 回填用户参数只登录成功页
            modelAndView.addObject("userInfo", user);
            // 判断是否为管理员
            if ("admin".equals(user.getAdminCode())) {
                modelAndView.setViewName("admin");
            } else {
                modelAndView.setViewName("home");
            }
        } else {
            // 健壮性判断，无该用户返回注册登录页
            modelAndView.setViewName("redirect:/");
        }
        return modelAndView;
    }

    // 登录页
    @RequestMapping
    public String index() {
        return "index";
    }

}
