package com.scetop.meeting.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginSuccessController {
    @RequestMapping("/success/{id}")
    public ModelAndView home(@PathVariable(name = "id") String id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("id", id);
        modelAndView.setViewName("redirect:/pages/home.html");
        return modelAndView;
    }

}
