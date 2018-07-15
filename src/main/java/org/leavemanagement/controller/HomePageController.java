package org.leavemanagement.controller;

import org.leavemanagement.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * Created by atul_saurabh on 19/11/17.
 */

@Controller
@RequestMapping("/")
public class HomePageController
{
    @GetMapping
    public String homePage(Model model)
    {
        model.addAttribute("user",new User());
       return "home";
    }
}
