package org.leavemanagement.controller;


import org.leavemanagement.dto.EmployeeDTO;
import org.leavemanagement.dto.EmployeeUpdateDTO;
import org.leavemanagement.dto.SearchDTO;
import org.leavemanagement.entity.*;
import org.leavemanagement.service.EmployeeCatagoryService;
import org.leavemanagement.service.EmployeeService;
import org.leavemanagement.service.LeaveService;
import org.leavemanagement.status.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.rmi.server.RMIClassLoader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

    @Autowired
    private LeaveProperty leaveProperty;

    @PostMapping("/createcatagory.do")
    public String createEmployeeCatagory(@ModelAttribute("catagory")EmployeeCatagory catagory,Model model)
    {
        EmployeeCatagory existingCatagory = employeeCatagoryService.getAllEmployeeCatagories().stream().filter(catagory1 -> {
            return catagory1.getAbbriviation().toUpperCase().equals(catagory.getAbbriviation().toUpperCase());
        }).findFirst().get();
        if(existingCatagory != null)
        {
            model.addAttribute("catagory_add_message","Catagory Exists");
            return "welcome";
        }
        catagory.setAbbriviation(catagory.getAbbriviation().toUpperCase());
         if(employeeCatagoryService.addCatagory(catagory))
         {
             model.addAttribute("catagory_add_message","Catagory Created Successfully");
         }
         else
         {
             model.addAttribute("catagory_add_message","Catagory Created Failed");
         }
        //model.addAttribute("catagory",new EmployeeCatagory());
        return "welcome";
    }


    @PostMapping("/createdepartment.do")
   public String createDepartment(@ModelAttribute("department")Department department,Model model)
   {
       if(employeeService.getAllDepartments().stream().anyMatch(department1 -> {
           return department.getDepartmentabbriviation().toUpperCase().equals(department1.getDepartmentabbriviation().toUpperCase());
       }))
       {
           model.addAttribute("catagory_add_message","Department Already Exists");
           return "welcome";
       }
       department.setDepartmentabbriviation(department.getDepartmentabbriviation().toUpperCase());
      if(employeeService.addDepartment(department))
      {
          model.addAttribute("catagory_add_message","Department Created Successfully");
      }
      else
      {
          model.addAttribute("catagory_add_message","Department Creation Failed");
      }

      return "welcome";
   }

   @PostMapping("employeeadd.do")
    public String addEmployee(@ModelAttribute("employeedto")EmployeeDTO dto,Model model)
   {
       Employee existingEmployee = employeeService.getEmployeeById(dto.getEmployeeid().toUpperCase());
       if(existingEmployee != null)
       {
           model.addAttribute("catagory_add_message","Employee Already Exist With Id "+dto.getEmployeeid().toUpperCase());
           return "welcome";
       }

       Employee employee = new Employee();
       employee.setDepartment(employeeService.getDepartmentFromId(dto.getDepartmentid()));
       EmployeeCatagory employeeCatagory = employeeCatagoryService.getEmployeeCatagoryById(dto.getEmployeecatagoryid());
       employee.setCatagory(employeeCatagory);
       employee.setEmployeename(dto.getEmployeename());
       employee.setEmployeeid(dto.getEmployeeid().toUpperCase());
       List<LeaveCatagory> leaveCatagories = leaveService.getAllLeaveCatagory();
       int i=0;
       for(LeaveCatagory leavecat : leaveCatagories)
       {
           EmployeeLeave el = new EmployeeLeave();
           el.setLeaveCatagory(leavecat);
           if(dto.isOldEmployee())
           {
               employee.setDateofjoing(dto.getDateofjoing());
               el.setBalance(dto.getAllowednoleaves()[i]);
               el.setTotalApplicable(dto.getAllowednoleaves()[i]);
               employee.setStatus(EmployeeStatus.ACTIVE);
           }
           else
           {
               employee.setStatus(EmployeeStatus.NEW);
               employee.setDateofjoing(dto.getDateofjoing());
               boolean isaplicable = leaveProperty.isLeaveApplicable(employeeCatagory.getAbbriviation(),leavecat.getAbbriviation());
               el.setLeaveCatagory(leavecat);
               if(!isaplicable) {
                   el.setBalance(0);
                   el.setTotalApplicable(0);
               }
               else
               {

                   float defaultValue = leaveProperty.defaultValue(employeeCatagory.getAbbriviation(),leavecat.getAbbriviation());
                   if(leavecat.getAbbriviation().equalsIgnoreCase(LeaveType.LEAVE_RH) ||
                           leavecat.getAbbriviation().equalsIgnoreCase(LeaveType.LEAVE_LWP) ||
                           leavecat.getAbbriviation().equalsIgnoreCase(LeaveType.LEAVE_DL) ||
                           leavecat.getAbbriviation().equalsIgnoreCase(LeaveType.LEAVE_SPCL)
                           )
                   {
                       el.setTotalApplicable(defaultValue);
                       el.setBalance(defaultValue);
                   }
                   else
                       if(leavecat.getAbbriviation().equalsIgnoreCase(LeaveType.LEAVE_CL))
                   {
                       LocalDate thisDate = LocalDate.parse(employee.getDateofjoing(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                       long noofdays = 0;
                       float balance=0.0f;
                       int month = thisDate.getMonthValue();
                       if(month>= MonthProperty.JANUARY && month <= MonthProperty.JUNE)
                       {
                           String june = "30/06/"+thisDate.getYear();
                           LocalDate thirtyJune = LocalDate.parse(june, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                           noofdays = ChronoUnit.DAYS.between(thisDate,thirtyJune);


                       }
                       else
                       {
                           String dec = "31/12/"+thisDate.getYear();
                           LocalDate thirty1stDec = LocalDate.parse(dec, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                           noofdays = ChronoUnit.DAYS.between(thisDate,thirty1stDec);

                       }

                       long divFactor = (long)Math.floor(180.0/defaultValue);
                       long band = noofdays / divFactor;
                       long rem = noofdays % divFactor;
                       long gross = leaveProperty.getGross("CL");
                       balance = balance + band;
                       if(rem >=15 && rem<gross)
                           balance+=0.5f;
                       else
                       if(rem >=gross)
                           balance+=1.0f;
                       el.setTotalApplicable(balance);
                       el.setBalance(balance);
                   }
                   else
                   {
                       el.setBalance(0);
                       el.setTotalApplicable(0);
                   }

                  /* LocalDate thisDate = LocalDate.parse(dto.getDateofjoing(),DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                   int month = thisDate.getMonthValue();
                   float balance=0.0f;
                   long noofdays = 0;
                   if(month>= MonthProperty.JANUARY && month <= MonthProperty.JUNE)
                   {
                       String june = "30/06/"+thisDate.getYear();
                       LocalDate thirtyJune = LocalDate.parse(june, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                       noofdays = ChronoUnit.DAYS.between(thisDate,thirtyJune);


                   }
                   else
                   {
                       String dec = "31/12/"+thisDate.getYear();
                       LocalDate thirty1stDec = LocalDate.parse(dec, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                       noofdays = ChronoUnit.DAYS.between(thisDate,thirty1stDec);

                   }

                   if(leavecat.getAbbriviation().equalsIgnoreCase(LeaveType.LEAVE_RH) ||
                           leavecat.getAbbriviation().equalsIgnoreCase(LeaveType.LEAVE_LWP) ||
                           leavecat.getAbbriviation().equalsIgnoreCase(LeaveType.LEAVE_DL) ||
                           leavecat.getAbbriviation().equalsIgnoreCase(LeaveType.LEAVE_SPCL)
                           )
                   {
                       el.setTotalApplicable(defaultValue);
                       el.setBalance(defaultValue);
                   }
                   else
                   {
                       long divFactor = (long)Math.floor(180.0/defaultValue);
                       long band = noofdays / divFactor;
                       long rem = noofdays % divFactor;
                       long gross = leaveProperty.getGross(leavecat.getAbbriviation());
                       balance = balance + band;
                       if(rem >=15 && rem<gross)
                           balance+=0.5f;
                       else
                           if(rem >=gross)
                               balance+=1.0f;
                       el.setTotalApplicable(balance);
                       el.setBalance(balance);

                   }*/
               }
           }

           i++;
           employee.getLeaves().add(el);

       }
       if(employeeService.addEmployee(employee))
           model.addAttribute("catagory_add_message","Employee Added Successfully");
       else
           model.addAttribute("catagory_add_message","Employee Addition Failed");
       return "welcome";
   }


  @PostMapping("employeeupdate.do")
    public String updateEmployeeRecord(@ModelAttribute("employee")EmployeeUpdateDTO employee,Model model)
   {
        Employee updatedEmployee = employeeService.getEmployeeById(employee.getOldemployeeid().toUpperCase());
        if(!updatedEmployee.getEmployeeid().equals(employee.getEmployeeid().toUpperCase()))
        {
              Employee newEmployee = new Employee();
              newEmployee.setEmployeeid(employee.getEmployeeid().toUpperCase());
              newEmployee.setStatus(updatedEmployee.getStatus());
              newEmployee.setDateofjoing(employee.getDateofjoing());
              newEmployee.setCatagory(updatedEmployee.getCatagory());
              newEmployee.setDepartment(updatedEmployee.getDepartment());
              newEmployee.setEmployeename(updatedEmployee.getEmployeename());
              updatedEmployee.getLeaves().stream().forEach(employeeLeave -> {
                  EmployeeLeave leave=new EmployeeLeave();
                  leave.setBalance(employeeLeave.getBalance());
                  leave.setTotalApplicable(employeeLeave.getTotalApplicable());
                  leave.setLeaveCatagory(employeeLeave.getLeaveCatagory());
                  newEmployee.getLeaves().add(leave);
              });
              updatedEmployee.getLeaveApplications().stream().forEach(leaveApplication -> {
                 LeaveApplication leaveApplication1=new LeaveApplication();
                 leaveApplication1.setEmployee(newEmployee);
                 leaveApplication.getApplicationDays().stream().forEach(applicationDays -> {
                     ApplicationDays applicationDays1=new ApplicationDays();
                     applicationDays1.setStatus(applicationDays.getStatus());
                     applicationDays1.setHalfday(applicationDays.isHalfday());
                     applicationDays1.setFirsthalf(applicationDays.isFirsthalf());
                     applicationDays1.setAppliedDate(applicationDays.getAppliedDate());
                     leaveApplication1.getApplicationDays().add(applicationDays1);
                 });
                // leaveApplication1.setApplicationDays(leaveApplication.getApplicationDays());
                 leaveApplication1.setStatus(leaveApplication.getStatus());
                 leaveApplication1.setFirstDayAsHalfDay(leaveApplication.isFirstDayAsHalfDay());
                 leaveApplication1.setHalfdayApplicable(leaveApplication.isHalfdayApplicable());
                 leaveApplication1.setLeavetype(newEmployee.getLeaves().stream().filter(employeeLeave -> {
                     return employeeLeave.getLeaveCatagory().getAbbriviation().equals(leaveApplication.getLeavetype().getLeaveCatagory().getAbbriviation());
                 }).findFirst().get());
                 leaveApplication1.setNoofdays(leaveApplication.getNoofdays());
                 leaveApplication1.setReason(leaveApplication.getReason());
                 leaveApplication1.setToDate(leaveApplication.getToDate());
                 leaveApplication1.setFromDate(leaveApplication.getFromDate());
                 leaveApplication1.setApplicationDate(leaveApplication.getApplicationDate());
                 newEmployee.getLeaveApplications().add(leaveApplication1);
              });
            if(employeeService.terminateEmployee(updatedEmployee))
            {
                if(employeeService.addEmployee(newEmployee))
                {
                    model.addAttribute("catagory_add_message","Employee Updated Successfully");
                }
                else
                {
                    model.addAttribute("catagory_add_message","Employee Not Updated Successfully");
                }
            }
            else
            {
                model.addAttribute("catagory_add_message","Employee Not Updated Successfully");
            }
        }
        else
        {
            updatedEmployee.setEmployeename(employee.getEmployeename());
            updatedEmployee.setDateofjoing(employee.getDateofjoing());
            updatedEmployee.setEmployeeid(employee.getEmployeeid());
            updatedEmployee.setCatagory(employeeCatagoryService.getEmployeeCatagoryById(employee.getEmployeecatagoryid()));
            updatedEmployee.setDepartment(employeeService.getDepartmentFromId(employee.getDepartmentid()));
            updatedEmployee.setStatus(EmployeeStatus.ACTIVE);
            int i=0;
            for (EmployeeLeave l : updatedEmployee.getLeaves())
            {
                l.setTotalApplicable(employee.getAllowednoleaves()[i]);
                l.setBalance(employee.getAllowednoleaves()[i]);
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
        }


        return "welcome";

   }

   @GetMapping("/logout.do")
    public String logout(Model model)
   {
       model.addAttribute("user",new User());
       return "home";
   }

   @PostMapping("/deleteemployee.do")
    public String deleteEmployee(@RequestParam("empid")String empid,@RequestParam("terminateddate")String terminateddate,Model model)
   {
       Employee employee = employeeService.getEmployeeById(empid.toUpperCase());
       if(employee.getStatus() != null && employee.getStatus().equals(EmployeeStatus.TERMINATED))
       {
           model.addAttribute("message","Employee Not Exist");
           return "deletemessage";
       }
       /*TerminatedEmployee terminatedEmployee=new TerminatedEmployee();
       terminatedEmployee.setEmployeeid(employee.getEmployeeid());
       terminatedEmployee.setEmployeename(employee.getEmployeename());
       terminatedEmployee.setDepartment(employee.getDepartment().getDepartmentabbriviation());
       terminatedEmployee.setEmployeetype(employee.getCatagory().getAbbriviation());
       float elclosing = employee.getLeaves().stream().filter(employeeLeave -> {
           return employeeLeave.getLeaveCatagory().getAbbriviation().equals(LeaveProperty.EL);
       }).findFirst().get().getBalance();
       float cslclosing =employee.getLeaves().stream().filter(employeeLeave -> {
           return employeeLeave.getLeaveCatagory().getAbbriviation().equals(LeaveProperty.CSL);
       }).findFirst().get().getBalance();

       terminatedEmployee.setElclosingbalance(elclosing);
       terminatedEmployee.setCslclosingbalance(cslclosing);*/

       LocalDate terminatedDate=LocalDate.parse(terminateddate,DateTimeFormatter.ofPattern("dd/MM/yyyy"));
       int month=terminatedDate.getMonthValue();
       int year=terminatedDate.getYear();
       LocalDate comparingDate=null;
       if(month>=1 && month <=6)
       {
           comparingDate=LocalDate.parse(MonthProperty.THIRTY_JUNE+year,DateTimeFormatter.ofPattern("dd/MM/yyyy"));
       }
       else
       {
           comparingDate=LocalDate.parse(MonthProperty.THIRTY_FIRST_DECEMBER+year,DateTimeFormatter.ofPattern("dd/MM/yyyy"));
       }

       long noofdays = ChronoUnit.DAYS.between(terminatedDate,comparingDate);
       LeaveApplication leaveApplication=new LeaveApplication();
       leaveApplication.setStatus(LeaveStatus.LEAVE_ALL_ACTIVE);
       leaveApplication.setNoofdays(noofdays);
       leaveApplication.setReason(EmployeeStatus.HALF_TERMINATED);
       leaveApplication.setEmployee(employee);
       leaveApplication.setFromDate(terminatedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
       leaveApplication.setToDate(comparingDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
       EmployeeLeave lwpLeave = employee.getLeaves().stream().filter(employeeLeave -> {
           return employeeLeave.getLeaveCatagory().getAbbriviation().equals(LeaveType.LEAVE_LWP);
       }).findFirst().get();
       lwpLeave.setBalance(lwpLeave.getBalance()-noofdays);
       leaveApplication.setLeavetype(lwpLeave);
       leaveApplication.setHalfdayApplicable(false);
       leaveApplication.setFirstDayAsHalfDay(false);
       leaveApplication.setApplicationDate(terminatedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

       for(int i=0;i<=noofdays;i++)
       {
           LocalDate nextdate = terminatedDate.plusDays(i);
           ApplicationDays applicationDays=new ApplicationDays();
           applicationDays.setAppliedDate(nextdate.format(DateTimeFormatter.ofPattern("dd/MM/yyy")));
           applicationDays.setHalfday(false);
           applicationDays.setFirsthalf(false);
           applicationDays.setStatus(DayStatus.DAY_ACTIVE);
           leaveApplication.getApplicationDays().add(applicationDays);
       }
       employee.setStatus(EmployeeStatus.HALF_TERMINATED);
       TerminatedEmployee terminatedEmployee=new TerminatedEmployee();
       terminatedEmployee.setEmployeename(employee.getEmployeename());
       terminatedEmployee.setEmployeeid(employee.getEmployeeid());
       terminatedEmployee.setDepartment(employee.getDepartment().getDepartmentabbriviation());
       terminatedEmployee.setElclosingbalance(employee.getLeaves().stream().filter(employeeLeave -> {
         return employeeLeave.getLeaveCatagory().getAbbriviation().equals(LeaveType.LEAVE_EL);
       }).findFirst().get().getBalance());
       terminatedEmployee.setCslclosingbalance(employee.getLeaves().stream().filter(employeeLeave -> {
           return employeeLeave.getLeaveCatagory().getAbbriviation().equals(LeaveType.LEAVE_SICK);
       }).findFirst().get().getBalance());
       terminatedEmployee.setEmployeetype(employee.getCatagory().getAbbriviation());
       terminatedEmployee.setDoj(employee.getDateofjoing());
       terminatedEmployee.setDor(terminateddate);
       employee.getLeaveApplications().add(leaveApplication);

           if(employeeService.deleteEmployee(terminatedEmployee))
           {

               if (employeeService.updateEmployee(employee))
               {
                   model.addAttribute("message","Employee Deleted");
               }
               else
               {
                   model.addAttribute("message","Employee Deleted");
               }
           }

           else
               model.addAttribute("message","Employee Not Deleted");
       return "deletemessage";
   }


   @PostMapping("/changepassword.do")
   public String changePassword(@RequestParam("newpassword")String newpassword,Model model)
   {
       if(employeeService.changeUserPassword(newpassword))
       {
           model.addAttribute("catagory_add_message","Password Updated Successfully");
       }
       else
       {
           model.addAttribute("catagory_add_message","Password Not Updated Successfully");
       }
       return "welcome";
   }


   @PostMapping("/borrowleave.do")
    public String borrowLeaveAmount(@RequestParam("empid")String empid,@RequestParam("leaveid")int leaveid,
                                    @RequestParam("borrowamount")float borrowamount,Model model)
   {
            if(employeeService.borrowLeaveAmountAndUpdate(empid,leaveid,borrowamount))
            {
                model.addAttribute("catagory_add_message","Borrow Successful");
            }
            else
            {
                model.addAttribute("catagory_add_message","Borrow Not Successful");
            }
            return "welcome";
   }

}
