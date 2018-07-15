package org.leavemanagement.controller;


import ch.qos.logback.core.joran.conditional.ElseAction;
import org.leavemanagement.dto.EmployeeAndLeave;
import org.leavemanagement.dto.UnrestrictedLWP;
import org.leavemanagement.entity.*;
import org.leavemanagement.service.EmployeeService;
import org.leavemanagement.service.LeaveService;
import org.leavemanagement.service.UserService;
import org.leavemanagement.status.*;
import org.leavemanagement.task.Task1Jan;
import org.leavemanagement.task.Task1stJuly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.DateFormatter;
import java.text.DateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/leaveoperation")
public class LeaveOperationController
{
    @Autowired
    private Task1Jan task1Jan;

    @Autowired
    private Task1stJuly task1stJuly;

    @Autowired
    private LeaveService leaveService;
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LeaveProperty leaveProperty;

    @Autowired
    private UserService userService;

    private boolean allupdated = true;

    private float no_of_days_return=0;

    @PostMapping("/createcatagory.do")
    public String createLeaveCatagory(@ModelAttribute("leavecatagory")LeaveCatagory leaveCatagory, Model model)
    {
        if(leaveService.getAllLeaveCatagory().stream().anyMatch(leaveCatagory1 -> {
            return leaveCatagory1.getAbbriviation().toUpperCase().equals(leaveCatagory.getAbbriviation().toUpperCase());
        }))
        {
            model.addAttribute("catagory_add_message","Leave Catagory Already Exists");
            return "welcome";
        }
        leaveCatagory.setAbbriviation(leaveCatagory.getAbbriviation().toUpperCase());
        if(leaveService.addLeaveCatagory(leaveCatagory))
        {
            model.addAttribute("catagory_add_message","Leave Catagory Created Successfully");
        }
        else
        {
            model.addAttribute("catagory_add_message","Leave Catagory Creation Failed");
        }

        return "welcome";
    }

    @PostMapping("/applyleave.do")
    public String applyLeave(@RequestBody EmployeeAndLeave employeeandleave,Model model)
    {
        String MESSAGE_TEMPLATE="";
        List<LocalDate> localDates = new ArrayList<>();
        Employee employee = employeeService.getEmployeeById(employeeandleave.getEmployee().getEmployeeid().toUpperCase());
        if(employee.getStatus() != null && employee.getStatus().equals(EmployeeStatus.TERMINATED))
        {
            model.addAttribute("message","Employee is Not available");
            return "leaverevertmessage";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate startDate = LocalDate.parse(employeeandleave.getLeaveApplication().getFromDate(),formatter);
        LocalDate endDate = LocalDate.parse(employeeandleave.getLeaveApplication().getToDate(),formatter);
        EmployeeLeave leave = leaveService.getLeaveById(employeeandleave.getLeaveid());
            /*if(employeeService.isDateInRange(employee,startDate,endDate,employeeandleave.getLeaveApplication().isFirstDayAsHalfDay()))
            {
                model.addAttribute("message","Leave Can Not Be Applied In Same Period Of Time");
            }*/
          //else
            //{
                float balance = leaveService.getLeaveBalance(employeeandleave.getLeaveid());
                if (balance < employeeandleave.getLeaveApplication().getNoofdays() && !leave.getLeaveCatagory().getAbbriviation().equals(LeaveProperty.LWP)) {
                    model.addAttribute("message", "Sorry, Insufficient Balance");

                } else {

                    employeeandleave.getLeaveApplication().setLeavetype(leave);
                    String today = LocalDate.now().format(formatter);

                    LeaveApplication leaveApplication = employeeandleave.getLeaveApplication();
                    leaveApplication.setEmployee(employee);
                    //leaveApplication.setApplicationDate(LocalDate.now().format(formatter));
                    leaveApplication.setStatus(LeaveStatus.LEAVE_ALL_ACTIVE);
                    leaveApplication.setFirstDayAsHalfDay(false);


                    leaveApplication.setHalfdayApplicable(false);

                    for (int i=0;i<employeeandleave.getDays().length;i++)
                    {
                        String leavedate = employeeandleave.getDays()[i];
                        String [] leavedatesplit = leavedate.split("_");
                        String nextDate = leavedatesplit[0];
                        LocalDate nextLocalDate = LocalDate.parse(nextDate,DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        ApplicationDays days = new ApplicationDays();

                        if(leavedatesplit[1].equals("FH")) {
                            if(employeeService.isDateInRange(employee,nextLocalDate,nextLocalDate,true))
                            {
                                MESSAGE_TEMPLATE = MESSAGE_TEMPLATE+" "+nextDate+",";
                              continue;
                            }
                            leaveApplication.setHalfdayApplicable(true);
                            if(i == 0) {
                                leaveApplication.setFirstDayAsHalfDay(true);
                            }
                            days.setHalfday(true);
                            days.setFirsthalf(true);
                            leave.setBalance(leave.getBalance() - 0.5f);
                        }
                        else if(leavedatesplit[1].equals("SH"))
                        {
                            if(employeeService.isDateInRange(employee,nextLocalDate,nextLocalDate,false))
                            {
                                MESSAGE_TEMPLATE = MESSAGE_TEMPLATE+" "+nextDate+",";
                               continue;
                            }
                            leaveApplication.setHalfdayApplicable(true);
                            if(i == 0) {
                                leaveApplication.setFirstDayAsHalfDay(true);
                            }
                            days.setHalfday(true);
                            days.setFirsthalf(false);
                            leave.setBalance(leave.getBalance() - 0.5f);
                        }
                        else {
                            if(employeeService.isDateInRange(employee,nextLocalDate,nextLocalDate,false))
                            {
                                MESSAGE_TEMPLATE = MESSAGE_TEMPLATE+" "+nextDate+",";
                              continue;
                            }
                            days.setHalfday(false);
                            days.setFirsthalf(false);
                            //leaveApplication.setHalfdayApplicable(false);
                            //leaveApplication.setFirstDayAsHalfDay(false);
                            leave.setBalance(leave.getBalance() - 1.0f);
                        }
                        days.setAppliedDate(nextDate);
                        days.setStatus(DayStatus.DAY_ACTIVE);
                        leaveApplication.getApplicationDays().add(days);
                    }

                    if(leaveApplication.getApplicationDays().size() == 0)
                    {
                        model.addAttribute("message", "Attention.." + MESSAGE_TEMPLATE + " is Not Applied");
                    }
                    else {
                        employee.getLeaveApplications().add(leaveApplication);
                        if (employeeService.updateEmployee(employee)) {
                            long employeeonleave = employeeService.getAllEmployee().stream().filter(employee1 -> {
                                return employee1.getDepartment().getDepartmentabbriviation().equalsIgnoreCase(employee.getDepartment().getDepartmentabbriviation()) &&
                                        employee1.getLeaveApplications().stream().anyMatch(leaveApplication1 -> {
                                            return leaveApplication1.getApplicationDays().stream().anyMatch(applicationDays -> {
                                                return applicationDays.getStatus().equals(DayStatus.DAY_ACTIVE) && employeeandleave.getLeaveApplication().getFromDate().equals(applicationDays.getAppliedDate());

                                            });
                                        });
                            }).count();
                            long employeeCount = employeeService.getAllEmployee().stream().filter(employee1 -> {
                                return employee1.getDepartment().getDepartmentabbriviation().equalsIgnoreCase(employee.getDepartment().getDepartmentabbriviation());
                            }).count();
                            int onleave = (int) Math.ceil((employeeCount * 15f) / 100f);

                                if (MESSAGE_TEMPLATE.equals(""))
                                {
                                    if(onleave <= employeeonleave)
                                    model.addAttribute("message", "Leave Applied Successfully But 15% Employee Are On Leave");
                                    else
                                        model.addAttribute("message", "Leave Applied Successfully");
                                }

                                else {
                                    if(onleave <= employeeonleave)
                                    model.addAttribute("message", "Attention.." + MESSAGE_TEMPLATE + " is Not Applied And 15% Employee Are On Leave");
                                    else
                                        model.addAttribute("message", "Attention.." + MESSAGE_TEMPLATE + " is Not Applied");

                                }
                        } else
                            model.addAttribute("message", "Leave Not Applied Successfully");
                    }
                }
            //}
        return "leaverevertmessage";
    }


    @PostMapping("/revertfullleaveinemployee.do")
    public String revertFullLeaveToEmployee(@RequestParam("empid")String empid,
                                            @RequestParam("leaveid")int leaveid,
                                            @RequestParam("applycriteria")int applycriteria, Model model)
    {
        Employee employee = employeeService.getEmployeeById(empid.toUpperCase());
        if(employee.getStatus() != null && employee.getStatus().equals(EmployeeStatus.TERMINATED))
        {
            model.addAttribute("message","Employee Not Exist");
            return "leaverevertmessage";
        }
        Predicate<LeaveApplication> leaveApplicationPredicate = leaveApplication -> {
          return leaveApplication.getLeaveapplicationid() == leaveid;
        };
        LeaveApplication leaveApplication = employee.getLeaveApplications().stream().filter(leaveApplicationPredicate).findAny().get();

        Predicate<EmployeeLeave> employeeLeavePredicate = employeeLeave -> {
          return  employeeLeave.getLeaveCatagory().getAbbriviation().equals(leaveApplication.getLeavetype().getLeaveCatagory().getAbbriviation());
        };
        EmployeeLeave leave=employee.getLeaves().stream().filter(employeeLeavePredicate).findFirst().get();

        Predicate<ApplicationDays> applicationDaysPredicate = applicationDays -> {
            return applicationDays.getStatus().equals(DayStatus.DAY_ACTIVE);
        };
        leaveApplication.getApplicationDays().stream().forEach(applicationDays -> {
                    int year = Integer.parseInt(applicationDays.getAppliedDate().split("/")[2]);
                    int month = Integer.parseInt(applicationDays.getAppliedDate().split("/")[1]);
                    switch (applycriteria) {
                        case 0:
                    /*
                    * REVERT THE CURRENT SEMESTER LEAVE
                    * */
                            if (applicationDaysPredicate.test(applicationDays))
                                leave.setBalance(leave.getBalance() + (applicationDays.isHalfday() ? 0.5f : 1.0f));
                            break;
                        case 1:
                    /*
                    * REVERT THE PREVIOUS SEMESTER
                    * */

                    /*
                    * PREVIOUS SEMESTER IS LAST YEAR
                    * */
                            if (month >= MonthProperty.JANUARY && month <= MonthProperty.JUNE) {
                      /*
                      * IF THE LEAVE IS ACCUMULATIVE
                      * */
                                if (leaveApplication.getLeavetype().getLeaveCatagory().isAccumulated()) {
                        /*
                        * IF THE DAY IS ACTIVE, REVERT THE BALANCE AND TOTAL APPLICABLE
                        * */
                                    if (applicationDaysPredicate.test(applicationDays)) {
                                        leave.setBalance(leave.getBalance() + (applicationDays.isHalfday() ? 0.5f : 1.0f));
                                        leave.setTotalApplicable(leave.getTotalApplicable() + (applicationDays.isHalfday() ? 0.5f : 1.0f));
                                    }

                                }
                      /*
                      * IF THE LEAVE IS NON ACUMMULATED THEN NO ACTION SHOULD BE TAKEN
                      * */
                                else {
                                    // NO ACTION
                                }

                            }
                    /*
                    * PREVIOUS SEMESTER IS THE LAST SEMESTER
                    * */
                            else {

                       /*
                       * LEAVE IS CARRY FORWARDED
                       * */
                                if (leave.getLeaveCatagory().isCarryForwarded()) {
                                    if (applicationDaysPredicate.test(applicationDays)) {
                                        leave.setBalance(leave.getBalance() + (applicationDays.isHalfday() ? 0.5f : 1.0f));
                                        leave.setTotalApplicable(leave.getTotalApplicable() + (applicationDays.isHalfday() ? 0.5f : 1.0f));
                                    }
                                }
                            }

                            break;
                        case 2:
                    /*
                    * NO NEED TO DO PERFORM ANY ACTON
                    * */
                            break;
                    }
                });


        leaveApplication.getApplicationDays().parallelStream().forEach(applicationDays1 -> {
            applicationDays1.setStatus(DayStatus.DAY_INACTIVE);
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            applicationDays1.setRevertedDate(today);
        });
        leaveApplication.setStatus(LeaveStatus.LEAVE_DEACTIVATED);
        //employee.getLeaveApplications().remove(leaveApplication);
        if(employeeService.updateEmployee(employee))
          model.addAttribute("message","Leave Reverted Successfully");
        else
            model.addAttribute("message","Leave Not Reverted");
        return "leaverevertmessage";

    }


    @PostMapping("/revertpartialleaveinemployee.do")
    public String getPartialLeaveRevertBackForm(@RequestParam("empid")String empid,@RequestParam("leaveid") int leaveid,
                                                @RequestParam("applycriteria")int applycriteria,
                                                Model model)
    {
        Employee employee = employeeService.getEmployeeById(empid.toUpperCase());
        if(employee.getStatus()!=null && employee.getStatus().equals(EmployeeStatus.TERMINATED))
        {
            model.addAttribute("catagory_add_message","Employee Not Exist");
            return "welcome";
        }
        Predicate<LeaveApplication> leaveApplicationPredicate = leaveApplication -> {
          return leaveApplication.getLeaveapplicationid() == leaveid;
        };
        LeaveApplication leaveApplication=employee.getLeaveApplications().parallelStream().filter(leaveApplicationPredicate).findFirst().get();
        model.addAttribute("employee",employee);
        model.addAttribute("leaveapp",leaveApplication);
        model.addAttribute("applycriteria",applycriteria);
        return "partialleaverevert";

    }

    @PostMapping("/revertpartialleaves.do")
    public String revertPartialLeaves(@RequestParam("leavedateid")int [] leavedateid,
                                      @RequestParam(name = "halfday",required = false) int [] halfday,
                                      @RequestParam(name = "firsthalf",required = false)int [] firsthalf,
                                      @RequestParam("employeeid")String employeeid,
                                      @RequestParam("leaveid")int leaveid,
                                      @RequestParam("applycriteria")int applycriteria,
                                      Model model)
    {

      Employee employee = employeeService.getEmployeeById(employeeid.toUpperCase());
      if(employee.getStatus() != null && employee.getStatus().equals(EmployeeStatus.TERMINATED))
      {
          model.addAttribute("catagory_add_message","Employee Not Exist");
          return "welcome";
      }
      Predicate<LeaveApplication> leaveApplicationPredicate = leaveApplication -> {
          return leaveApplication.getLeaveapplicationid() == leaveid;
      };
      LeaveApplication leaveApplication = employee.getLeaveApplications().parallelStream().filter(leaveApplicationPredicate).findFirst().get();
      leaveApplication.getApplicationDays().stream().forEach(applicationDays -> {
           int j=0;
           for(j=0;j<leavedateid.length;j++)
           {
               if(applicationDays.getDateid() == leavedateid[j]) {
                   EmployeeLeave l = employee.getLeaves().parallelStream().filter(employeeLeave -> {
                       return employeeLeave.getLeaveCatagory().getAbbriviation().equals(leaveApplication.getLeavetype().getLeaveCatagory().getAbbriviation());
                   }).findFirst().get();

                   if (halfday != null && Arrays.stream(halfday).anyMatch(value -> {
                       return value == applicationDays.getDateid();
                   })) {
                       switch (applycriteria)
                       {
                           case 0:
                               /*
                               * ADJUST THE CURRENT YEAR BALANCE
                               * */
                               l.setBalance(l.getBalance() + 0.5f);
                               break;
                           case 1:
                               /*
                               * ADJUST THE PREVIOUS YEAR
                               * */
                               int month = LocalDate.now().getMonthValue();
                               /*
                               * LAST SEMESTER IS LAST YEAR
                               * */
                               if(month >= MonthProperty.JANUARY && month <= MonthProperty.JUNE){
                                   /*
                                   * IF THE LEAVE IS ACCUMULATIVE
                                   * */
                                   if(leaveApplication.getLeavetype().getLeaveCatagory().isAccumulated())
                                   {
                                     l.  setBalance(l.getBalance() + 0.5f);
                                     l.setTotalApplicable(l.getTotalApplicable()+0.5f);
                                   }

                               }
                               else
                               {
                                   if(leaveApplication.getLeavetype().getLeaveCatagory().isCarryForwarded())
                                   {
                                       l.setBalance(l.getBalance() +0.5f);
                                       l.setTotalApplicable(l.getTotalApplicable()+0.5f);

                                   }
                               }

                               l.setBalance(l.getBalance() + 0.5f);
                               l.setTotalApplicable(l.getTotalApplicable()+0.5f);
                               break;
                           case 2:
                               break;

                       }
                       applicationDays.setHalfday(true);
                       applicationDays.setStatus(DayStatus.DAY_ACTIVE);
                       applicationDays.setRevertedDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                   } else {
                       switch (applycriteria)
                       {
                           case 0:
                               l.setBalance(l.getBalance()+(applicationDays.isHalfday() ? 0.5f : 1.0f));
                               break;
                           case 1:
                               int month=LocalDate.now().getMonthValue();
                               if(month >= MonthProperty.JANUARY && month <= MonthProperty.JUNE){
                                   /*
                                   * IF THE LEAVE IS ACCUMULATIVE
                                   * */
                                   if(leaveApplication.getLeavetype().getLeaveCatagory().isAccumulated())
                                   {
                                       l.  setBalance(l.getBalance() + (applicationDays.isHalfday() ? 0.5f : 1.0f));
                                       l.setTotalApplicable(l.getTotalApplicable()+(applicationDays.isHalfday() ? 0.5f : 1.0f));
                                       }

                               }
                               else
                               {
                                   if(leaveApplication.getLeavetype().getLeaveCatagory().isCarryForwarded())
                                   {
                                       l.setBalance(l.getBalance() +(applicationDays.isHalfday() ? 0.5f : 1.0f));
                                       l.setTotalApplicable(l.getTotalApplicable()+(applicationDays.isHalfday() ? 0.5f : 1.0f));

                                   }
                               }
                               break;
                           case 2:
                               break;
                       }

                       applicationDays.setStatus(DayStatus.DAY_INACTIVE);
                       applicationDays.setRevertedDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                   }


                   if (firsthalf!= null && Arrays.stream(firsthalf).anyMatch(value -> {
                       return value == applicationDays.getDateid();
                   })) {
                       applicationDays.setStatus(DayStatus.DAY_ACTIVE);
                       applicationDays.setFirsthalf(true);
                   }

               }
           }
      });
      long count = leaveApplication.getApplicationDays().stream().filter(applicationDays -> {
          return !applicationDays.getStatus().equals(DayStatus.DAY_INACTIVE);
      }).count();
      if(count == 0)
          leaveApplication.setStatus(LeaveStatus.LEAVE_DEACTIVATED);
      else
          leaveApplication.setStatus(LeaveStatus.LEAVE_PARTIAL_ACTIVE);
      if(employeeService.updateEmployee(employee))
      model.addAttribute("catagory_add_message","Leave Reverted Successfully");
      else
      model.addAttribute("catagory_add_message","Leave NOT Reverted Successfully");
       return "welcome";
    }


    @PostMapping("/updateemployeeleave.do")
    public String assignExistingLeaveToEmployee(@RequestParam("leaveid")int leaveid,
                                                @RequestParam("catagoryid")int catagoryid,
                                                @RequestParam("defaultvalue")float defaultvalue,Model model)
    {

        employeeService.getAllEmployee().stream().filter(employee ->
        {
            return employee.getCatagory().getCatagoryid() == catagoryid
                    && (employee.getStatus() == null || employee.getStatus().equals(EmployeeStatus.ACTIVE)) ;
        })
                .collect(Collectors.toList()).stream().filter(employee ->
        {
            return !employee.getLeaves().stream().anyMatch(employeeLeave -> {
                return employeeLeave.getLeaveCatagory().getCatagoryid()  == leaveid;
            });
        })
                .forEach(employee -> {
            EmployeeLeave employeeLeave = new EmployeeLeave();
            LeaveCatagory leaveCatagory = leaveService.getAllLeaveCatagory().stream().filter(leaveCatagory1 -> {
                return leaveCatagory1.getCatagoryid() == leaveid;
            }).findFirst().get();

            employeeLeave.setLeaveCatagory(leaveCatagory);
            employeeLeave.setTotalApplicable(defaultvalue);
            employeeLeave.setBalance(defaultvalue);
            employee.getLeaves().add(employeeLeave);
            if(employeeService.updateEmployee(employee))
                allupdated = true;
            else
                allupdated = false;

        });

        if(allupdated)
            model.addAttribute("catagory_add_message","Leave Assigned To All Employees");
        else
            model.addAttribute("catagory_add_message","Leave Not Assigned To All Employee");

        return "welcome";
    }

    @PostMapping("/declareholiday.do")
    public String declareHoliday(@RequestParam("fromdate")String fromdate,
                                  @RequestParam("todate")String todate,
                                 @RequestParam("leavetype")String leavetype,
                                 @RequestParam("catagoryid")int catagoryid,Model model
                                 )
    {
       employeeService.getAllEmployee().stream().filter(employee -> {
          return employee.getCatagory().getCatagoryid() == catagoryid
                  && (employee.getStatus() == null || employee.getStatus().equals(EmployeeStatus.ACTIVE)) ;
       }).forEach(employee -> {
           LeaveApplication application = new LeaveApplication();
           application.setStatus(LeaveStatus.LEAVE_ALL_ACTIVE);
           application.setEmployee(employee);
           EmployeeLeave leave = new EmployeeLeave();
           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
           long noofdays = ChronoUnit.DAYS.between(LocalDate.parse(fromdate,formatter),LocalDate.parse(todate,formatter));
           leave.setBalance(noofdays);
           leave.setTotalApplicable(noofdays);
           LeaveCatagory leaveCatagory = new LeaveCatagory();
           leaveCatagory.setAccumulated(false);
           leaveCatagory.setCarryForwarded(false);
           leaveCatagory.setAbbriviation(LeaveType.LEAVE_HLD);
           leaveCatagory.setCatagoryname(leavetype);
           leave.setLeaveCatagory(leaveCatagory);
           application.setLeavetype(leave);
           application.setApplicationDate(fromdate);
           LocalDate startdate = LocalDate.parse(fromdate,formatter);

           for (long i=0;i<=noofdays;i++)
           {
                LocalDate nextDate = startdate.plusDays(i);
                ApplicationDays days = new ApplicationDays();
                days.setStatus(DayStatus.DAY_ACTIVE);
                days.setHalfday(false);
                days.setFirsthalf(false);
                days.setAppliedDate(nextDate.format(formatter));
                Predicate<ApplicationDays> applicationDaysPredicate = applicationDays -> {
                    return applicationDays.getStatus().equals(DayStatus.DAY_ACTIVE)
                            && applicationDays.getAppliedDate().equals(nextDate.format(formatter));
                };

                // If holiday is declared then return previously applied leave in that period
                employee.getLeaveApplications().stream().filter(leaveApplication -> {
                    return leaveApplication.getApplicationDays().stream().anyMatch(applicationDaysPredicate);
                }).forEach(leaveApplication -> {
                    leaveApplication.getApplicationDays().
                            stream().
                            filter(applicationDaysPredicate).
                            forEach(applicationDays -> {
                                applicationDays.setStatus(DayStatus.DAY_INACTIVE);
                                no_of_days_return += (applicationDays.isHalfday() ? 0.5f : 1f);
                            });

                    if(leaveApplication.getApplicationDays().stream().filter(applicationDays -> {
                        return applicationDays.getStatus().equals(DayStatus.DAY_ACTIVE);
                    }).count() == 0)
                        leaveApplication.setStatus(LeaveStatus.LEAVE_DEACTIVATED);
                    else
                        leaveApplication.setStatus(LeaveStatus.LEAVE_PARTIAL_ACTIVE);

                    EmployeeLeave employeeLeave=employee.getLeaves().stream().filter(employeeLeave1 -> {
                        return employeeLeave1.getLeaveCatagory().getAbbriviation().equals(leaveApplication.getLeavetype().getLeaveCatagory().getAbbriviation());
                    }).findFirst().get();
                    employeeLeave.setBalance(employeeLeave.getBalance()+no_of_days_return);
                    no_of_days_return=0;
                });

                application.getApplicationDays().add(days);
           }

           employee.getLeaves().add(leave);
           application.setFirstDayAsHalfDay(false);
           application.setFromDate(fromdate);
           application.setToDate(todate);
           application.setNoofdays(noofdays);
           application.setHalfdayApplicable(false);
           application.setReason("Holiday");
           employee.getLeaveApplications().add(application);
           if(employeeService.updateEmployee(employee))
               allupdated = true;
           else
               allupdated = false;

       });


        if(allupdated)
            model.addAttribute("catagory_add_message","Holiday Declared");
        else
            model.addAttribute("catagory_add_message","Holiday Not Declared");

     return "welcome";
    }

    @PostMapping("/fetchadvleaveapplicationform.do")
    public String getAdvancedLeaveApplyForm(@RequestParam("empid")String empid,@RequestParam("applycriteria")int applycriteria, Model model)
    {
        Employee employee = employeeService.getEmployeeById(empid.toUpperCase());
        if(employee.getStatus() != null && employee.getStatus().equals(EmployeeStatus.TERMINATED))
        {
            model.addAttribute("message","Employee Not Exist");
            return "employeenotexist";
        }
        LeaveApplication leaveApplication=new LeaveApplication();
        EmployeeAndLeave employeeAndLeave = new EmployeeAndLeave();
        employeeAndLeave.setEmployee(employee);
        employeeAndLeave.setLeaveApplication(leaveApplication);
        model.addAttribute("employeeandleave",employeeAndLeave);
        switch (applycriteria)
        {
            case 1:
                model.addAttribute("searchcriteria","previoussemester");
                break;
            case 2:
                model.addAttribute("searchcriteria","nextsemester");
                break;
        }
        return "advanceleaveapplication";
    }

    @PostMapping("/applyadvanceleave.do")
    public String applyAdvancedLeave(@ModelAttribute("employeeandleave") EmployeeAndLeave employeeandleave,Model model)
    {
        EmployeeLeave employeeLeave = leaveService.getLeaveById(employeeandleave.getLeaveid());
        Employee employee = employeeService.getEmployeeById(employeeandleave.getEmployee().getEmployeeid().toUpperCase());
        if (employee.getStatus() != null && employee.getStatus().equals(EmployeeStatus.TERMINATED))
        {
            model.addAttribute("catagory_add_message","Employee Not Exist");
            return "welcome";
        }
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();
        float currentTotalApplied = employee.getLeaves().stream().filter(employeeLeave1 -> {
            return employeeLeave1.getLeaveid() == employeeandleave.getLeaveid();
        }).findFirst().get().getTotalApplicable();
        float currentBalance = employee.getLeaves().stream().filter(employeeLeave1 -> {
            return employeeLeave1.getLeaveid() == employeeandleave.getLeaveid();
        }).findFirst().get().getBalance();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate startDate = LocalDate.parse(employeeandleave.getLeaveApplication().getFromDate(),formatter);
        LocalDate endDate = LocalDate.parse(employeeandleave.getLeaveApplication().getToDate(),formatter);
        long noofdays = ChronoUnit.DAYS.between(startDate,endDate)+1;
        List<LocalDate> alldates = new ArrayList<>();
        for(int i=0;i<=noofdays;i++)
        {
            LocalDate nextDate = startDate.plusDays(i);
            alldates.add(nextDate);
        }

        if (!leaveProperty.isLeaveApplicable(employee.getCatagory().getAbbriviation(),employeeLeave.getLeaveCatagory().getAbbriviation()))
        {
           model.addAttribute("catagory_add_message","THIS LEAVE IS NOT APPLICABLE");
        }
        else
        {

            switch(employeeandleave.getSearchcriteria())
            {
                case "nextsemester":

                    /*
                    * CALCULATION OF LEAVE APPLIED IN NEXT SEMESTER
                    * */
                   employee.getLeaveApplications().stream().filter(leaveApplication -> {
                       return leaveApplication.getLeavetype().getLeaveid() == employeeandleave.getLeaveid();
                   }).filter(leaveApplication -> {
                       return leaveApplication.getApplicationDays().stream().anyMatch(applicationDays -> {
                           String [] dates=applicationDays.getAppliedDate().split("/");
                           int month = Integer.parseInt(dates[1]);
                           int year = Integer.parseInt(dates[2]);
                           if (currentMonth>=1 && currentMonth <=6)
                            return applicationDays.getStatus().equals(DayStatus.DAY_ACTIVE) && (month >=6 && month<=12) && year == currentYear;
                          else
                               return applicationDays.getStatus().equals(DayStatus.DAY_ACTIVE) && (month >=1 && month<=6) && year == currentYear+1;
                       });
                   }).forEach(leaveApplication -> {
                       leaveApplication.getApplicationDays().stream().forEach(applicationDays -> {
                           if (applicationDays.getStatus().equals(DayStatus.DAY_ACTIVE))
                               no_of_days_return+= (applicationDays.isHalfday() ? 0.5f : 1.0f);
                       });
                   });

                   /*
                   * GET THE DEFAULT VALUE OF APPLIED LEAVE
                   * */
                   float defaultValue = leaveProperty.defaultValue(employee.getCatagory().getAbbriviation(),employeeLeave.getLeaveCatagory().getAbbriviation());
                   if (defaultValue - no_of_days_return - employeeandleave.getLeaveApplication().getNoofdays() >=0)
                   {
                       no_of_days_return=0.0f;
                      /*
                      * FIND IF LEAVE IS ALREADY APPLIED IN SAME PERIOD OF TIME
                      *
                      * */

                      if (isLeaveAlreadyApplied(employee,alldates))
                      {
                     // ALREADY APPLIED ON THE GIVEN RANGE
                          model.addAttribute("catagory_add_message","LEAVE CAN NOT BE APPLIED IN SAME PERIOD OF TIME");
                      }
                      else
                      {
                         /*
                         * APPLY FOR THE LEAVE BUT DO NOT UPDATE THE BALANCE
                         * */

                          if (addLeaveToEmployee(employeeandleave,false,0))
                              model.addAttribute("catagory_add_message", "Leave Applied Successfully");
                          else
                              model.addAttribute("catagory_add_message", "Leave Not Applied Successfully");
                      }

                   }
                   else {
                      model.addAttribute("catagory_add_message","SORRY, INSUFFICIENT BALANCE");
                   }

                    break;
                case "previoussemester":
                    /*
                    * PREVIOUS SEMSTER IS LAST YEAR
                    * */
                    if (currentMonth >=1 && currentMonth <= 6)
                    {
                        float defaultValue1 = leaveProperty.defaultValue(employee.getCatagory().getAbbriviation(),employeeLeave.getLeaveCatagory().getAbbriviation());

                        /*
                        * IF LEAVE IS ACCUMULATED TYPE
                        * */
                        if (employeeLeave.getLeaveCatagory().isAccumulated())
                        {

                            /*
                             * CALCULATE PREVIOUS YEAR BALANCE
                             * */
                            float previousYearBalance = currentTotalApplied - defaultValue1;
                            /*
                            * IF BALANCE IS LESS THAN NO OF DAYS APPLIED FOR LEAVE
                            * */

                            if(previousYearBalance < alldates.size() || currentBalance - alldates.size() < 0)
                            {
                                model.addAttribute("catagory_add_message","SORRY, INSUFFICIENT BALANCE");
                            }
                            /*
                            * IF BALANCE IS ENOUGH TO APPLY THE LEAVE
                            * */
                            else
                            {
                                /*
                                * CHECK LEAVE IS ALREADY APPLIED IN THAT PERIOD
                                * */
                                if (isLeaveAlreadyApplied(employee,alldates))
                                {
                                    model.addAttribute("catagory_add_message","LEAVE CAN NOT BE APPLIED IN SAME PERIOD OF TIME");
                                }
                                /*
                                * IF LEAVE IS NOT ALREADY APPLIED THEN APPLY THE LEAVE AND UPDATE THE CURRENT BALANCE
                                * */
                                else
                                {
                                   if(addLeaveToEmployee(employeeandleave,true,currentTotalApplied))
                                   {
                                       model.addAttribute("catagory_add_message", "Leave Applied Successfully");
                                   }
                                   else
                                   {
                                       model.addAttribute("catagory_add_message", "Leave Not Applied Successfully");
                                   }
                                }

                            }

                        }
                        /*
                        * IF LEAVE IS NON ACCUMULATED TYPE
                        * */
                        else
                            {
                            /*
                            * FIND LAST YEAR BALANCE FOR NON ACCUMULATED TYPE LEAVE
                            * */
                            employee.getLeaveApplications().stream().filter(leaveApplication -> {
                                return leaveApplication.getLeavetype().getLeaveid() == employeeandleave.getLeaveid();
                            }).filter(leaveApplication -> {
                                return leaveApplication.getApplicationDays().stream().anyMatch(applicationDays -> {
                                    Integer year=Integer.parseInt(applicationDays.getAppliedDate().split("/")[2]);
                                    return applicationDays.getStatus().equals(DayStatus.DAY_ACTIVE) &&
                                            year == currentYear -1;
                                });
                            }).forEach(leaveApplication -> {
                                leaveApplication.getApplicationDays().stream().forEach(applicationDays -> {
                                    if (applicationDays.getStatus().equals(DayStatus.DAY_ACTIVE))
                                        no_of_days_return+= (applicationDays.isHalfday() ? 0.5f : 1.0f);
                                });
                            });

                            /**
                             * GET THE DEFAULT VALUE OF THE LEAVE
                             * */
                            float defaultValue2 = leaveProperty.defaultValue(employee.getCatagory().getAbbriviation(),employeeLeave.getLeaveCatagory().getAbbriviation());
                            /*
                            * IF THE LEAVE IS CARRY FORWARDED IN SIX MONTH THEN IN A YEAR
                            * NUMBER OF LEAVE ACCUMULATED = 2* defaultValue
                            * ELSE
                            * LEAVE ACCUMULATED = defaultValue only
                            * */
                            float balance=0.0f;
                            if(employeeLeave.getLeaveCatagory().isCarryForwarded())
                            {
                               balance = 2 * defaultValue2 - no_of_days_return;
                            }
                            else
                            {
                                balance = defaultValue2 - no_of_days_return;
                            }
                            no_of_days_return = 0;

                            /*
                            * IF THERE IS NO ENOUGH BALANCE
                            * */

                            if(balance < alldates.size())
                            {
                                model.addAttribute("catagory_add_message","SORRY, INSUFFICIENT BALANCE");
                            }
                            else
                            {
                               /*
                               * CHECK IF LEAVE IS ALREADY APPLIED
                               * */
                               if(isLeaveAlreadyApplied(employee,alldates))
                               {
                                   model.addAttribute("catagory_add_message","LEAVE CAN NOT BE APPLIED IN SAME PERIOD OF TIME");
                               }
                               /*
                               * IF LEAVE IS NOT APPLIED
                               * */
                               else
                               {
                                   if(addLeaveToEmployee(employeeandleave,false,currentTotalApplied))
                                   {
                                       model.addAttribute("catagory_add_message", "Leave Applied Successfully");
                                   }
                                   else
                                   {
                                       model.addAttribute("catagory_add_message", "Leave Not Applied Successfully");
                                   }
                               }
                            }

                        }

                    }
                    /*
                        * PREVIOUS SEMESTER IS LAST SEMESTER
                        * */
                    else
                    {

                       /*
                       * IF THE LEAVE IS CARRY FORWARDED BETWEEN SEMESTERS
                       * */

                       if(employeeLeave.getLeaveCatagory().isCarryForwarded())
                       {

                           /*
                           * FIND THE DEFAULT VALUE OF THAT LEAVE
                           * */

                           float defaultValue3 = leaveProperty.defaultValue(employee.getCatagory().getAbbriviation(),employeeLeave.getLeaveCatagory().getAbbriviation());
                           /*
                           * FIND THE PREVIOUS SEMESTER BALANCE
                           *
                           * */



                           float previousSemesterBalance=currentTotalApplied- defaultValue3;

                           /*
                           * IF ENOUGH BALANCE IS NOT AVAILABLE
                           * */

                           if(previousSemesterBalance < alldates.size() || currentBalance - alldates.size() < 0)
                           {
                               model.addAttribute("catagory_add_message","SORRY, INSUFFICIENT BALANCE");
                           }
                           /*
                           * IF THERE IS ENOUGH BALANCE
                           * */

                           else
                           {
                               /*
                               * IF LEAVE IS ALREADY APPLIED
                               * */

                               if(isLeaveAlreadyApplied(employee,alldates))
                               {
                                   model.addAttribute("catagory_add_message","LEAVE CAN NOT BE APPLIED IN SAME PERIOD OF TIME");
                               }
                               /*
                               * IF LEAVE NOT APPLIED IN THAT SPAN
                               * */
                               else
                               {
                                   if(addLeaveToEmployee(employeeandleave,true,currentTotalApplied))
                                   {
                                       model.addAttribute("catagory_add_message", "Leave Applied Successfully");
                                   }
                                   else
                                   {
                                       model.addAttribute("catagory_add_message", "Leave Not Applied Successfully");
                                   }
                               }
                           }
                       }
                       /*
                       * IF THE LEAVE IS NOT CARRY FORWARDED
                       * */
                       else
                           {
                               /*
                               * FIND THE DEFAULT VALUE OF THAT LEAVE
                               * */

                               float defaultValue4 = leaveProperty.defaultValue(employee.getCatagory().getAbbriviation(),employeeLeave.getLeaveCatagory().getAbbriviation());
                               /*
                               * FIND THE LAST SEMESTER BALANCE
                               * */

                               employee.getLeaveApplications().stream().filter(leaveApplication -> {
                                   return leaveApplication.getLeavetype().getLeaveid() == employeeandleave.getLeaveid();
                               }).filter(leaveApplication -> {
                                   return leaveApplication.getApplicationDays().stream().anyMatch(applicationDays -> {
                                       Integer year=Integer.parseInt(applicationDays.getAppliedDate().split("/")[2]);
                                       Integer month=Integer.parseInt(applicationDays.getAppliedDate().split("/")[1]);
                                       return applicationDays.getStatus().equals(DayStatus.DAY_ACTIVE) &&
                                               (month >=1 && month <=6) &&
                                               year == currentYear;
                                   });
                               }).forEach(leaveApplication -> {
                                   leaveApplication.getApplicationDays().stream().forEach(applicationDays -> {
                                       if (applicationDays.getStatus().equals(DayStatus.DAY_ACTIVE))
                                           no_of_days_return+= (applicationDays.isHalfday() ? 0.5f : 1.0f);
                                   });
                               });


                               float lastSemesterBalance = defaultValue4 - no_of_days_return;

                               /*
                               * IF ENOUGH BALANCE IS NOT AVAILABLE
                               * */

                               if(lastSemesterBalance < alldates.size())
                               {
                                   model.addAttribute("catagory_add_message","SORRY, INSUFFICIENT BALANCE");
                               }
                               /*
                               * THERE IS ENOUGH BALANCE AVAILABLE
                               * */
                               else
                               {
                                   /*
                                   * CHECK ANY LEAVE IS ALREADY APPLIED IN THAT PERIOD OF TIME
                                   * */
                                   if(isLeaveAlreadyApplied(employee,alldates))
                                   {
                                       model.addAttribute("catagory_add_message","LEAVE CAN NOT BE APPLIED IN SAME PERIOD OF TIME");
                                   }
                                   else
                                   {
                                       if (addLeaveToEmployee(employeeandleave,false,currentTotalApplied))
                                       {
                                           model.addAttribute("catagory_add_message", "Leave Applied Successfully");
                                       }
                                       else
                                       {
                                           model.addAttribute("catagory_add_message", "Leave Not Applied Successfully");
                                       }
                                   }
                               }

                       }

                        

                    }
                    break;
            }
        }

        no_of_days_return=0;
       return "welcome";
    }

    private boolean isLeaveAlreadyApplied(Employee employee,List<LocalDate> alldates)
    {
        return employee.getLeaveApplications().stream().anyMatch(leaveApplication -> {
            return leaveApplication.getApplicationDays().stream().anyMatch(applicationDays -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate appDate = LocalDate.parse(applicationDays.getAppliedDate(),formatter);
                return alldates.stream().anyMatch(localDate -> {
                    return localDate.isEqual(appDate);
                }) && applicationDays.getStatus().equals(DayStatus.DAY_ACTIVE);
            });
        });
    }

    private boolean addLeaveToEmployee(EmployeeAndLeave employeeandleave,boolean updateBalance,float currentTotalApplied)
    {
        Employee employee = employeeService.getEmployeeById(employeeandleave.getEmployee().getEmployeeid().toUpperCase());
        EmployeeLeave employeeLeave = leaveService.getLeaveById(employeeandleave.getLeaveid());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate startDate = LocalDate.parse(employeeandleave.getLeaveApplication().getFromDate(),formatter);
        LocalDate endDate = LocalDate.parse(employeeandleave.getLeaveApplication().getToDate(),formatter);
        long noofdays = ChronoUnit.DAYS.between(startDate,endDate);
        employeeandleave.getLeaveApplication().setLeavetype(employeeLeave);
        String today = LocalDate.now().format(formatter);

        LeaveApplication leaveApplication = employeeandleave.getLeaveApplication();
        leaveApplication.setEmployee(employee);
        //leaveApplication.setApplicationDate(LocalDate.now().format(formatter));
        leaveApplication.setStatus(LeaveStatus.LEAVE_ALL_ACTIVE);
        leaveApplication.setFirstDayAsHalfDay(false);
        leaveApplication.setHalfdayApplicable(false);
        float no_of_leaves=0.0f;
        for (int i=0;i<employeeandleave.getDays().length;i++)
        {
            String [] dates= (employeeandleave.getDays()[i]).split("_");
            String nextDate = dates[0];
            ApplicationDays days = new ApplicationDays();
            LocalDate thisDate = LocalDate.parse(dates[0],DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            DayOfWeek dayOfWeek = DayOfWeek.of(thisDate.get(ChronoField.DAY_OF_WEEK));
            if (dayOfWeek == DayOfWeek.SUNDAY && employeeLeave.getLeaveCatagory().getAbbriviation().equalsIgnoreCase("CL"))
                continue;

            if(dates[1].equals("FH"))
            {
                leaveApplication.setHalfdayApplicable(true);
               if(i == 0)
                   leaveApplication.setFirstDayAsHalfDay(true);
                days.setHalfday(true);
                days.setFirsthalf(true);
                no_of_leaves+=0.5f;
            }
            else
                if(dates[1].equals("SH"))
                {
                    leaveApplication.setHalfdayApplicable(true);
                    if(i == 0)
                        leaveApplication.setFirstDayAsHalfDay(true);
                    days.setHalfday(true);
                    days.setFirsthalf(false);
                    no_of_leaves+=0.5f;
                }

            else {
                days.setHalfday(false);
                days.setFirsthalf(false);
                no_of_leaves+=1.0f;
            }
            days.setAppliedDate(nextDate);
            days.setStatus(DayStatus.DAY_ACTIVE);
            leaveApplication.getApplicationDays().add(days);
        }

        if(updateBalance)
        {
            employeeLeave.setBalance(employeeLeave.getBalance()-no_of_leaves);
            employeeLeave.setTotalApplicable(currentTotalApplied-no_of_leaves);
        }


        employee.getLeaveApplications().add(leaveApplication);
        return employeeService.updateEmployee(employee);
    }

    @PostMapping("/declarerh.do")
    public String declareRH(@RequestParam("fromdate")String fromdate,@RequestParam("catagoryid") int catagoryid,Model model)
    {
        List<Employee> employeeList = employeeService.getAllEmployee().stream().filter(employee -> {
            return employee.getCatagory().getCatagoryid() == catagoryid
                    && (employee.getStatus() == null || employee.getStatus().equals(EmployeeStatus.ACTIVE));
        }).collect(Collectors.toList());


        List<Employee> employeeHavingRHOnSameDate= employeeList.stream().filter(employee -> {
            return employee.getLeaveApplications().stream().anyMatch(leaveApplication -> {
                return leaveApplication.getLeavetype().getLeaveCatagory().getAbbriviation().equals("RH") &&
                        leaveApplication.getApplicationDays().stream().anyMatch(applicationDays -> {
                            return applicationDays.getStatus().equals(DayStatus.DAY_ACTIVE) && applicationDays.getAppliedDate().equals(fromdate);
                        });
            });
        }).collect(Collectors.toList());

        if(employeeList.size() < employeeHavingRHOnSameDate.size() *3)
        {
           return "rhalreadydec";
        }

        Map<Boolean,List<Employee>> employeeMap = employeeList.stream().collect(Collectors.partitioningBy(employee -> {
            return employee.getLeaveApplications().stream().anyMatch(leaveApplication -> {
                return leaveApplication.getApplicationDays().stream().anyMatch(applicationDays -> {
                    return applicationDays.getAppliedDate().equals(fromdate);
                });
            });
        }));

        List<Employee> leaveAppliedEmployee = employeeMap.get(Boolean.TRUE);
        List<Employee> leaveNotAppliedEmployee = employeeMap.get(Boolean.FALSE);
        List<Employee> employeeNotHavingRH=leaveAppliedEmployee.stream().filter(employee -> {
            return employee.getLeaves().stream().anyMatch(employeeLeave -> {
                return  employeeLeave.getLeaveCatagory().getAbbriviation().equals("RH") && employeeLeave.getBalance() == 0;
            });
        }).collect(Collectors.toList());

       // leaveNotAppliedEmployee.removeAll(employeeNotHavingRH);

        leaveNotAppliedEmployee.stream().forEach(employee -> {
            LeaveApplication leaveApplication = new LeaveApplication();
            EmployeeLeave leave = employee.getLeaves().stream().filter(employeeLeave -> {
                return employeeLeave.getLeaveCatagory().getAbbriviation().equals("RH");
            }).findFirst().get();

            leaveApplication.setEmployee(employee);
            leaveApplication.setLeavetype(leave);
            ApplicationDays applicationDays=new ApplicationDays();
            applicationDays.setAppliedDate(fromdate);
            applicationDays.setHalfday(false);
            applicationDays.setFirsthalf(false);
            applicationDays.setStatus(DayStatus.DAY_ACTIVE);
            leaveApplication.getApplicationDays().add(applicationDays);
            leaveApplication.setNoofdays(1.0f);
            leaveApplication.setReason("HOLIDAY");
            leaveApplication.setFromDate(fromdate);
            leaveApplication.setToDate(fromdate);
            leaveApplication.setHalfdayApplicable(false);
            leaveApplication.setFirstDayAsHalfDay(false);
            leaveApplication.setApplicationDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            leave.setBalance(leave.getBalance() - 1.0f);
            leaveApplication.setStatus(LeaveStatus.LEAVE_ALL_ACTIVE);
            employee.getLeaveApplications().add(leaveApplication);

            if(employeeService.updateEmployee(employee))
                allupdated=true;
            else
                allupdated=false;

        });

        model.addAttribute("employeealreadyappliedleave",leaveAppliedEmployee);
        model.addAttribute("employeenothavingrh",employeeNotHavingRH);
      if(allupdated)
          model.addAttribute("catagory_add_message","RH Applied Successfully");
      else
          model.addAttribute("catagory_add_message","RH Not Applied");

      return "reportrh";


    }



    @PostMapping("/schedule1stjan.do")
    public String doScheduling1stJan(@ModelAttribute("user")User user,Model model)
    {
        if(userService.userLogin(user.getUsername(),user.getPassword()))
        {
            if(task1Jan.scheduleRearrangeOfBalanceAtEndOfTheYear())
                model.addAttribute("catagory_add_message","SCHEDULING DONE");
            else
                model.addAttribute("catagory_add_message","SCHEDULING INTERRUPTED");
            return "welcome";
        }
        else
        {
            model.addAttribute("catagory_add_message","AUTHENTICATION FAILED FOR SCHEDULING");
            return "welcome";
        }

    }


    @PostMapping("/schedule1stjuly.do")
    public String doScheduling1stJuly(@ModelAttribute("user")User user, Model model)
    {
        if(userService.userLogin(user.getUsername(),user.getPassword()))
        {
            if(task1stJuly.scheduleReaggarngeOfLeaveBalance())
                model.addAttribute("catagory_add_message","SCHEDULING DONE");
            else
                model.addAttribute("catagory_add_message","SCHEDULING INTERRUPTED");
            return "welcome";
        }
        else
        {
            model.addAttribute("catagory_add_message","AUTHENTICATION FAILED FOR SCHEDULING");
            return "welcome";
        }

    }


    @PostMapping("/applyunrestrictedlwp.do")
    public String unrestrictedLWPApply(@RequestBody UnrestrictedLWP unrestrictedLWP,Model model)
    {
        Employee employee = employeeService.getEmployeeById(unrestrictedLWP.getEmployeeid().toUpperCase());
        LocalDate fromDate1 = LocalDate.parse(unrestrictedLWP.getFromdate(),DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDate toDate1 = LocalDate.parse(unrestrictedLWP.getTodate(),DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if(employeeService.isDateInRange(employee,fromDate1,toDate1,false))
        {
            model.addAttribute("message","Leave Can Not Be Applied In Same Period Of Time");
            return "leaverevertmessage";
        }
        EmployeeLeave employeeLeave=employee.getLeaves().stream().filter(employeeLeave1 -> {
            return employeeLeave1.getLeaveCatagory().getAbbriviation().equalsIgnoreCase(LeaveType.LEAVE_LWP);
        }).findFirst().get();
        LeaveApplication leaveApplication=new LeaveApplication();
        LocalDate fromDate = LocalDate.parse(unrestrictedLWP.getFromdate(),DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDate toDate = LocalDate.parse(unrestrictedLWP.getTodate(),DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        long noofdays = ChronoUnit.DAYS.between(fromDate,toDate);
        for (int i=0;i<=noofdays;i++)
        {
            LocalDate nextDate=fromDate.plusDays(i);
            ApplicationDays applicationDays=new ApplicationDays();
            applicationDays.setStatus(DayStatus.DAY_ACTIVE);
            applicationDays.setFirsthalf(false);
            applicationDays.setHalfday(false);
            applicationDays.setAppliedDate(nextDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            leaveApplication.getApplicationDays().add(applicationDays);
        }
        leaveApplication.setApplicationDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        leaveApplication.setFirstDayAsHalfDay(false);
        leaveApplication.setHalfdayApplicable(false);
        leaveApplication.setFromDate(unrestrictedLWP.getFromdate());
        leaveApplication.setToDate(unrestrictedLWP.getTodate());
        leaveApplication.setEmployee(employee);
        leaveApplication.setReason("LWP");
        leaveApplication.setNoofdays(noofdays);
        leaveApplication.setLeavetype(employeeLeave);
        leaveApplication.setStatus(LeaveStatus.LEAVE_ALL_ACTIVE);
        employee.getLeaveApplications().add(leaveApplication);
        if(employeeService.updateEmployee(employee))
        {
            model.addAttribute("message","LWP Applied Successfully");
        }
        else
        {
            model.addAttribute("message","LWP Not Applied Successfully");
        }
        return "leaverevertmessage";
    }


}
