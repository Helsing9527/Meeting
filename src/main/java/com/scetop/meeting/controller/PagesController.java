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

    @RequestMapping("/home/{id}")
    public ModelAndView home(@PathVariable(name = "id") String id) {
        User user = userServer.getById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("userName", user.getName());
        modelAndView.setViewName("home");
        return modelAndView;
    }

    @RequestMapping
    public String index() {
        return "index";
    }

}
