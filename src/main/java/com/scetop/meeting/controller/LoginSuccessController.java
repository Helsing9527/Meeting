package com.scetop.meeting.controller;

import com.scetop.meeting.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RestController
public class LoginSuccessController {
    @RequestMapping("/success")
    public ModelAndView home(User user) {
        System.out.println("success");
        System.out.println(user);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("username",user.getName());
        modelAndView.setViewName("redirect:/pages/home.html");
        return modelAndView;
    }

}
