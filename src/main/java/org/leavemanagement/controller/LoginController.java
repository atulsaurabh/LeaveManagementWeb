package org.leavemanagement.controller;

import org.leavemanagement.entity.User;
import org.leavemanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by atul_saurabh on 19/11/17.
 */

@Controller
@RequestMapping("/login.do")
public class LoginController
{
    @Autowired
    private UserService userService;

    @PostMapping
    public String login(@ModelAttribute("user")User user, Model model)
    {
          if (userService.userLogin(user.getUsername(),user.getPassword()))
              return "welcome";
          else {
                model.addAttribute("loginError","Invalid User Name Or Password");
                model.addAttribute("user",new User());
              return "home";
          }
    }
}
