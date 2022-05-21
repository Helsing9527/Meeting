package com.scetop.meeting.controller;

import com.scetop.meeting.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginSuccessController {
    @RequestMapping("/success")
    public ModelAndView home(User user) {
        System.out.println("success");
        System.out.println(user);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("redirect:/pages/home.html");
        return modelAndView;
    }

}
