package org.leavemanagement.controller;


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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.websocket.server.PathParam;
import java.util.List;

@Controller
@RequestMapping("/employeeoperation")
public class EmployeeOperationController
{
    @Autowired
    private EmployeeCatagoryService employeeCatagoryService;
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LeaveService leaveService;

    @PostMapping("/createcatagory.do")
    public String createEmployeeCatagory(@ModelAttribute("catagory")EmployeeCatagory catagory,Model model)
    {
         if(employeeCatagoryService.addCatagory(catagory))
         {
             model.addAttribute("catagory_add_message","Catagory Created Successfully");
         }
         else
         {
             model.addAttribute("catagory_add_message","Catagory Created Failed");
         }
        //model.addAttribute("catagory",new EmployeeCatagory());
        return "employeemaster";
    }


    @PostMapping("/createdepartment.do")
   public String createDepartment(@ModelAttribute("department")Department department,Model model)
   {
      if(employeeService.addDepartment(department))
      {
          model.addAttribute("catagory_add_message","Department Created Successfully");
      }
      else
      {
          model.addAttribute("catagory_add_message","Department Creation Failed");
      }

      return "employeemaster";
   }

   @PostMapping("employeeadd.do")
    public String addEmployee(@ModelAttribute("employeedto")EmployeeDTO dto,Model model)
   {
       Employee employee = new Employee();
       employee.setDepartment(employeeService.getDepartmentFromId(dto.getDepartmentid()));
       employee.setCatagory(employeeCatagoryService.getEmployeeCatagoryById(dto.getEmployeecatagoryid()));
       employee.setEmployeename(dto.getEmployeename());
       employee.setDateofjoing(dto.getDateofjoing());
       employee.setEmployeeid(dto.getEmployeeid());
       List<LeaveCatagory> leaveCatagories = leaveService.getAllLeaveCatagory();
       int i=0;
       for(LeaveCatagory leavecat : leaveCatagories)
       {
           EmployeeLeave el = new EmployeeLeave();
           el.setLeaveCatagory(leavecat);
           el.setBalance(dto.getAllowednoleaves()[i]);
           el.setTotalApplicable(dto.getAllowednoleaves()[i]);
           i++;
           employee.getLeaves().add(el);
       }
       if(employeeService.addEmployee(employee))
           model.addAttribute("catagory_add_message","Employee Added Successfully");
       else
           model.addAttribute("catagory_add_message","Employee Addition Failed");
       return "employeemaster";
   }


  @PostMapping("employeeupdate.do")
    public String updateEmployeeRecord(@ModelAttribute("employee")EmployeeUpdateDTO employee,Model model)
   {
        Employee updatedEmployee = employeeService.getEmployeeById(employee.getEmployeeid());
        updatedEmployee.setEmployeename(employee.getEmployeename());
        updatedEmployee.setDateofjoing(employee.getDateofjoing());
        updatedEmployee.setCatagory(employeeCatagoryService.getEmployeeCatagoryById(employee.getEmployeecatagoryid()));
        updatedEmployee.setDepartment(employeeService.getDepartmentFromId(employee.getDepartmentid()));
        int i=0;
        for (EmployeeLeave l : updatedEmployee.getLeaves())
        {
            l.setTotalApplicable(employee.getAllowednoleaves()[i]);
            i++;
        }
        if(employeeService.updateEmployee(updatedEmployee))
        {
            model.addAttribute("catagory_add_message","Employee Updated Successfully");
        }
        else
        {
            model.addAttribute("catagory_add_message","Employee Updation Failed");
        }

        return "employeemaster";

   }

}
