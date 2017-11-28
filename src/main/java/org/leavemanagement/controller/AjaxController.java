package org.leavemanagement.controller;


import org.leavemanagement.dto.EmployeeAndLeave;
import org.leavemanagement.dto.EmployeeDTO;
import org.leavemanagement.dto.EmployeeUpdateDTO;
import org.leavemanagement.dto.SearchDTO;
import org.leavemanagement.entity.*;
import org.leavemanagement.service.EmployeeCatagoryService;
import org.leavemanagement.service.EmployeeService;
import org.leavemanagement.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@Controller
@RequestMapping("/ajax")
public class AjaxController
{
    @Autowired
    private EmployeeCatagoryService employeeCatagoryService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private LeaveService leaveService;

    @GetMapping("/loadcatagory.do")
    public String getCatagoryPage(Model model)
    {
        model.addAttribute("catagory",new EmployeeCatagory());
        return "employeecatagory";
    }
    @GetMapping("/loaddepartment.do")
    public String getDepatmentPage(Model model)
    {
        model.addAttribute("department",new Department());
        return "department";
    }
    @GetMapping("/loademployeeadd.do")
    public String getEmployeeAddPage(Model model)
    {
        List<EmployeeCatagory> catagories = employeeCatagoryService.getAllEmployeeCatagories();
        List<Department> departments = employeeService.getAllDepartments();
        List<LeaveCatagory> leaves = leaveService.getAllLeaveCatagory();
        model.addAttribute("departmentlist",departments);
        model.addAttribute("catagorylist",catagories);
        model.addAttribute("employeedto",new EmployeeDTO());
        model.addAttribute("leavecatagorylist",leaves);
        return "employeeadd";
    }

    @GetMapping("/loadleavecatagory.do")
    public String getLeaveCatagoryPage(Model model)
    {
          model.addAttribute("leavecatagory",new LeaveCatagory());
          return "leavecatagory";
    }

    @GetMapping("/loademploysearch")
    public String getEmpSearchPage(Model model)
    {
        model.addAttribute("searchdto",new SearchDTO());
        return "employeeupdate";
    }

    @PostMapping("/searchemployee.do")
    public String searchResult(@RequestParam("empid")String empid, @RequestParam("searchall")Integer searchall, Model model)
    {
        String page="";
        //  int p = searchdto.isIncludeall() ? 1 : 0;
        switch(searchall)
        {
            case 0:
                Employee employee = employeeService.getEmployeeById(empid);
                EmployeeUpdateDTO employeeDTO = new EmployeeUpdateDTO();
                employeeDTO.setDateofjoing(employee.getDateofjoing());
                employeeDTO.setEmployeeid(employee.getEmployeeid());
                employeeDTO.setEmployeename(employee.getEmployeename());
                employeeDTO.setDepartmentid(employee.getDepartment().getDepartmentid());
                employeeDTO.setEmployeecatagoryid(employee.getCatagory().getCatagoryid());
                employeeDTO.setLeaves(employee.getLeaves());
                List<EmployeeCatagory> catagories = employeeCatagoryService.getAllEmployeeCatagories();
                List<Department> departments = employeeService.getAllDepartments();
                //List<LeaveCatagory> empleaves = leaveService.getAllLeaveCatagory();
                model.addAttribute("departmentlist",departments);
                model.addAttribute("catagorylist",catagories);
                //model.addAttribute("leavecatagorylist",empleaves);
                model.addAttribute("employee",employeeDTO);
                // model.addAttribute("searchdto",searchdto);
               // model.addAttribute("show_box",true);
                page="showsingleempresult";
                break;
            case 1:
                List<Employee> employeeList = employeeService.getAllEmployee();
                model.addAttribute("employeelist",employeeList);
                page="showmultiempresult";
                break;
            default:
                Employee emp = employeeService.getEmployeeById(empid);
                model.addAttribute("employee",emp);
                page="showsingleempresult";
                break;

        }
        return page;
    }

    @GetMapping("/loademployeeleaveapplication.do")
    public String getEmployeeSearchForm(Model model)
    {
       return "leaveapplicationemployeesearch";
    }


    @PostMapping("/fetchemployee.do")
    public String getEmployeeApplicationForm(@RequestParam("empid")String empid,Model model)
    {
      Employee employee = employeeService.getEmployeeById(empid);
      LeaveApplication leaveApplication=new LeaveApplication();
      EmployeeAndLeave employeeAndLeave = new EmployeeAndLeave();
      employeeAndLeave.setEmployee(employee);
      employeeAndLeave.setLeaveApplication(leaveApplication);
      model.addAttribute("employeeandleave",employeeAndLeave);
      return "leaveapplication";
    }
}
