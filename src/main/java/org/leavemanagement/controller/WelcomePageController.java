package org.leavemanagement.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/leavemanagement/")
public class WelcomePageController
{

    @GetMapping("/home.do")
    public String openHomePage()
    {
        return "welcome";
    }

    @GetMapping("/employeemaster.do")
    public String openEmployeeHub()
    {
        return "employeemaster";
    }

    @GetMapping("leavemaster.do")
    public String openLeaveHub()
    {
        return "leavemaster";
    }
}
