package org.leavemanagement.controller;


import org.leavemanagement.dto.EmployeeAndLeave;
import org.leavemanagement.entity.Employee;
import org.leavemanagement.entity.EmployeeLeave;
import org.leavemanagement.entity.LeaveApplication;
import org.leavemanagement.entity.LeaveCatagory;
import org.leavemanagement.service.EmployeeService;
import org.leavemanagement.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.function.Predicate;

@Controller
@RequestMapping("/leaveoperation")
public class LeaveOperationController
{

    @Autowired
    private LeaveService leaveService;
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/createcatagory.do")
    public String createLeaveCatagory(@ModelAttribute("leavecatagory")LeaveCatagory leaveCatagory, Model model)
    {
        if(leaveService.addLeaveCatagory(leaveCatagory))
        {
            model.addAttribute("catagory_add_message","Leave Catagory Created Successfully");
        }
        else
        {
            model.addAttribute("catagory_add_message","Leave Catagory Creation Failed");
        }

        return "leavemaster";
    }

    @PostMapping("/applyleave.do")
    public String applyLeave(@ModelAttribute("employeeandleave") EmployeeAndLeave employeeandleave,Model model)
    {
        Employee employee = employeeService.getEmployeeById(employeeandleave.getEmployee().getEmployeeid());

        if(employeeService.isLeaveAppliedInSpecifiedPeriod(employee,employeeandleave.getLeaveApplication().getFromDate()))
        {
            model.addAttribute("catagory_add_message","Leave Can Not Be Applied In Same Period Of Time");
        }
        else
        {
            float balance = leaveService.getLeaveBalance(employeeandleave.getLeaveid());
            if(balance <= employeeandleave.getLeaveApplication().getNoofdays())
            {
                model.addAttribute("catagory_add_message","Sorry, Insufficient Balance");

            }
            else
            {

                EmployeeLeave leave = leaveService.getLeaveById(employeeandleave.getLeaveid());
                leave.setBalance(leave.getBalance() - employeeandleave.getLeaveApplication().getNoofdays());
                employeeandleave.getLeaveApplication().setLeavetype(leave);
                employee.getLeaveApplications().add(employeeandleave.getLeaveApplication());

                if(employeeService.updateEmployee(employee))
                    model.addAttribute("catagory_add_message","Leave Applied Successfully");
                else
                    model.addAttribute("catagory_add_message","Leave Not Applied Successfully");

            }
        }

        return "leavemaster";
    }
}
