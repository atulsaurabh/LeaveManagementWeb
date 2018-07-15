package org.leavemanagement.controller;


import org.leavemanagement.dto.*;
import org.leavemanagement.entity.*;
import org.leavemanagement.service.EmployeeCatagoryService;
import org.leavemanagement.service.EmployeeService;
import org.leavemanagement.service.LeaveService;
import org.leavemanagement.status.EmployeeStatus;
import org.leavemanagement.status.LeaveType;
import org.leavemanagement.status.MonthProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        List<LeaveCatagory> leaves = leaveService.getAllLeaveCatagory().stream().filter(leaveCatagory -> {
            return !leaveCatagory.getAbbriviation().equals(LeaveType.LEAVE_HLD);
        }).collect(Collectors.toList());
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

    @GetMapping("/loademploysearch.do")
    public String getEmpSearchPage(Model model)
    {
        List<Department> departments = employeeService.getAllDepartments();
        model.addAttribute("searchdto",new SearchDTO());
        model.addAttribute("departments",departments);
        return "employeeupdate";
    }

    @PostMapping("/searchemployee.do")
    public String searchResult(@RequestParam(value = "empid",required = false)String empid,
                               @RequestParam(value = "deptid")int deptid,
                               @RequestParam("searchall")Integer searchall, Model model
                              )
    {
        String page="";
        //  int p = searchdto.isIncludeall() ? 1 : 0;
        switch(searchall)
        {
            case 0:
                Employee employee = employeeService.getEmployeeById(empid.toUpperCase());

                if(employee == null)
                {
                  List<Employee> employees = employeeService.getAllEmployee().stream().filter(employee1 -> {
                      return employee1.getEmployeename().toUpperCase().contains(empid.toUpperCase())
                              && (employee1.getStatus() == null || employee1.getStatus().equals(EmployeeStatus.ACTIVE) || employee1.getStatus().equals(EmployeeStatus.NEW));
                  }).collect(Collectors.toList());
                    model.addAttribute("employeelist",employees);
                    page="showmultiempresult";
                }
                else if(employee.getStatus() != null && employee.getStatus().equals(EmployeeStatus.TERMINATED))
                {
                    model.addAttribute("message","Employee Not Exist");
                    return "employeenotexist";
                }
                else {
                    EmployeeUpdateDTO employeeDTO = new EmployeeUpdateDTO();
                    employeeDTO.setOldemployeeid(employee.getEmployeeid());
                    employeeDTO.setDateofjoing(employee.getDateofjoing());
                    employeeDTO.setEmployeeid(employee.getEmployeeid().toUpperCase());
                    employeeDTO.setEmployeename(employee.getEmployeename());
                    employeeDTO.setDepartmentid(employee.getDepartment().getDepartmentid());
                    employeeDTO.setEmployeecatagoryid(employee.getCatagory().getCatagoryid());
                    List<EmployeeLeave> leaves = employee.getLeaves().stream().filter(employeeLeave -> {
                        return !employeeLeave.getLeaveCatagory().getAbbriviation().equals(LeaveType.LEAVE_HLD);
                    }).collect(Collectors.toList());
                    employeeDTO.setLeaves(leaves);
                    List<EmployeeCatagory> catagories = employeeCatagoryService.getAllEmployeeCatagories();
                    List<Department> departments = employeeService.getAllDepartments();
                    //List<LeaveCatagory> empleaves = leaveService.getAllLeaveCatagory();
                    model.addAttribute("departmentlist", departments);
                    model.addAttribute("catagorylist", catagories);
                    //model.addAttribute("leavecatagorylist",empleaves);
                    model.addAttribute("employee", employeeDTO);
                    // model.addAttribute("searchdto",searchdto);
                    // model.addAttribute("show_box",true);
                    page = "showsingleempresult";
                }
                break;
            case 1:
                List<Employee> employeeList=null;
                if (empid != null)
                {
                     employeeList = employeeService.getAllEmployee().stream().filter(employee1 -> {
                        return (employee1.getDepartment().getDepartmentid() == deptid) &&
                                (
                                        employee1.getEmployeename().toUpperCase().contains(empid.toUpperCase()) ||
                                        employee1.getEmployeeid().toUpperCase().equals(empid.toUpperCase())
                                )
                              && (employee1.getStatus() == null || employee1.getStatus().equals(EmployeeStatus.ACTIVE) || employee1.getStatus().equals(EmployeeStatus.NEW));
                    }).collect(Collectors.toList());
                }
                else {
                     employeeList = employeeService.getAllEmployee().stream().filter(employee1 -> {
                        return employee1.getDepartment().getDepartmentid() == deptid
                                && (employee1.getStatus() == null || employee1.getStatus().equals(EmployeeStatus.ACTIVE) || employee1.getStatus().equals(EmployeeStatus.NEW));
                    }).collect(Collectors.toList());
                }
                model.addAttribute("employeelist",employeeList);
            page="showmultiempresult";
                break;
            default:
                Employee emp = employeeService.getEmployeeById(empid.toUpperCase());
                if(emp.getStatus() != null || emp.getStatus().equals(EmployeeStatus.TERMINATED))
                {
                    model.addAttribute("message","Employee Not Exist");
                    return "employeenotexist";
                }
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
      Employee employee = employeeService.getEmployeeById(empid.toUpperCase());
      if((employee.getStatus() != null && employee.getStatus().equals(EmployeeStatus.TERMINATED)) ||
              (employee.getStatus() != null && employee.getStatus().equals(EmployeeStatus.HALF_TERMINATED)))
      {
          model.addAttribute("message","Employee Not Exist");
          return "employeenotexist";
      }
      List<EmployeeLeave> leaves = employee.getLeaves();
      leaves.sort(Comparator.comparing(employeeLeave -> {
          return employeeLeave.getLeaveCatagory().getAbbriviation();
      }));
      LeaveApplication leaveApplication=new LeaveApplication();
      EmployeeAndLeave employeeAndLeave = new EmployeeAndLeave();
      employeeAndLeave.setEmployee(employee);
      employeeAndLeave.setLeaveApplication(leaveApplication);
      model.addAttribute("employeeandleave",employeeAndLeave);
      return "leaveapplication";
    }

    @PostMapping("/fetchemployeeleaveapplication.do")
    public String fetchAllLeaveApplication(@RequestParam("empid")String empid,Model model)
    {
        /*Employee employee = employeeService.getEmployeeById(empid);
        List<LeaveApplication> leaveApplications = employeeService.getLast10LeaveApplication(empid);
       model.addAttribute("employee",employee);
       model.addAttribute("leaveapplications",leaveApplications);*/
       model.addAttribute("employeeid",empid.toUpperCase());
       //return "employeeleaveapplicationhistory";
        return "revertcriteria";
    }

    @PostMapping("revertleavewithcriteria.do")
    public String fetchAllLeaveApplication(@RequestParam("empid")String empid,@RequestParam("applycriteria")int applycriteria,Model model)
    {
        Employee employee = employeeService.getEmployeeById(empid.toUpperCase());
        if((employee.getStatus() != null && employee.getStatus().equals(EmployeeStatus.TERMINATED)) ||
                (employee.getStatus() != null && employee.getStatus().equals(EmployeeStatus.HALF_TERMINATED)))
        {
            model.addAttribute("message","Employee Not Exist");
            return "employeenotexist";
        }
        model.addAttribute("employee",employee);
        List<LeaveApplication> leaveApplications=null;
        int thisMonth = LocalDate.now().getMonthValue();
        int thisYear = LocalDate.now().getYear();

        switch (applycriteria)
        {
            case 0:
                //FETCH CURRENT SEMESTER LEAVE
                if (thisMonth >= MonthProperty.JANUARY && thisMonth<=MonthProperty.JUNE)
                 leaveApplications =employeeService.getLast10LeaveApplication(empid.toUpperCase(),MonthProperty.JANUARY,MonthProperty.JUNE,thisYear);
                else
                   leaveApplications=employeeService.getLast10LeaveApplication(empid.toUpperCase(),MonthProperty.JULY,MonthProperty.DECEMBER,thisYear);
                break;
            case 1:
                /*
                * PREVIOUS SEMESTER IS LAST YEAR
                * */
                if(thisMonth >= MonthProperty.JANUARY && thisMonth<=MonthProperty.JUNE)
                {
                    leaveApplications=employeeService.getLast10LeaveApplication(empid.toUpperCase(),MonthProperty.JULY,MonthProperty.DECEMBER,thisYear-1);
                }
                /*
                    * PREVIOUS SEMESTER IS LAST SEMESTER IN SAME YEAR
                    * */
                else
                {
                    leaveApplications=employeeService.getLast10LeaveApplication(empid.toUpperCase(),MonthProperty.JANUARY,MonthProperty.JUNE,thisYear);
                }

                break;
            case 2:
                /*
                * NEXT SEMESTER IS NEXT SEMESTER IN SAME YEAR
                * */
                if(thisMonth >= MonthProperty.JANUARY && thisMonth<=MonthProperty.JUNE)
                {
                    leaveApplications=employeeService.getLast10LeaveApplication(empid.toUpperCase(),MonthProperty.JULY,MonthProperty.DECEMBER,thisYear);
                }
                /*
                    * PREVIOUS SEMESTER IS NEXT SEMESTER IN NEXT YEAR
                    * */
                else
                {
                    leaveApplications=employeeService.getLast10LeaveApplication(empid.toUpperCase(),MonthProperty.JANUARY,MonthProperty.JUNE,thisYear+1);
                }
                break;
        }


        model.addAttribute("leaveapplications",leaveApplications);
        model.addAttribute("applycriteria",applycriteria);
        return "employeeleaveapplicationhistory";
    }

    @GetMapping("/loademployeeleaverevert.do")
    public String getEmployeeLeaveApplicationList()
    {
      return "leaveapplicationrevertsearch";
    }


    @GetMapping("/loadaggregatereportform.do")
    public String getAggregateReport(Model model)
    {
        List<Department> departments = employeeService.getAllDepartments();
        List<LeaveCatagory> employeeLeaves = employeeService.getAllEmployeeLeaves()
                .stream().filter(leaveCatagory -> {
                    return leaveCatagory.getAbbriviation().equals("EL")
                            || leaveCatagory.getAbbriviation().equals("CL") ||
                               leaveCatagory.getAbbriviation().equals("CSL");
                }).collect(Collectors.toList());

        List<EmployeeCatagory> employeeCatagories = employeeCatagoryService.getAllEmployeeCatagories();
        model.addAttribute("departments",departments);
        model.addAttribute("employeeleavecatagories",employeeLeaves);
        model.addAttribute("employeecatagories",employeeCatagories);
        model.addAttribute("aggregatereport",new AggregateReportDTO());
        return "aggregatereport";
    }

    @GetMapping("/loadleaveondayform.do")
    public String getLeaveOnDayForm(Model model)
    {
        List<LeaveCatagory> leaveCatagories = leaveService.getAllLeaveCatagory();
        List<Department> departments = employeeService.getAllDepartments();
        model.addAttribute("leavecatagory",leaveCatagories);
        model.addAttribute("departments",departments);
        return "dailyleavereportform";
    }

    @GetMapping("/loadleaveondayformxlsx.do")
    public String getLeaveOnDayFormXLS(Model model)
    {
        List<LeaveCatagory> leaveCatagories = leaveService.getAllLeaveCatagory();
        List<Department> departments = employeeService.getAllDepartments();
        model.addAttribute("leavecatagory",leaveCatagories);
        model.addAttribute("departments",departments);
        return "dailyleavereportformxlsx";
    }

    @GetMapping("/loadleaveondayformxls.do")
    public String getLeaveOnDayFormXLSX(Model model)
    {
        List<LeaveCatagory> leaveCatagories = leaveService.getAllLeaveCatagory();
        List<Department> departments = employeeService.getAllDepartments();
        model.addAttribute("leavecatagory",leaveCatagories);
        model.addAttribute("departments",departments);
        return "dailyleavereportformxls";
    }

    @GetMapping("/loadapplicationondayform.do")
    public String getApplicationOnDay(Model model)
    {
        List<Department> departments=employeeService.getAllDepartments();
        model.addAttribute("departments",departments);
        return "applicationonaday";
    }

    @GetMapping("/loadapplicationondayformxls.do")
    public String getApplicationOnDayXls(Model model)
    {
        List<Department> departments=employeeService.getAllDepartments();
        model.addAttribute("departments",departments);
        return "applicationonadayxls";
    }

    @GetMapping("/loadapplicationondayformxlsx.do")
    public String getApplicationOnDayXlsx(Model model)
    {
        List<Department> departments=employeeService.getAllDepartments();
        model.addAttribute("departments",departments);
        return "applicationonadayxlsx";
    }

    @GetMapping("/loadassignexistingleave.do")
    public String getLeaveForAssignment(Model model)
    {
        List<LeaveCatagory> leaveCatagories = leaveService.getAllLeaveCatagory();
        List<EmployeeCatagory> employeeCatagories = employeeCatagoryService.getAllEmployeeCatagories();
        model.addAttribute("leaves",leaveCatagories);
        model.addAttribute("catagorylist",employeeCatagories);
        return "assignleavetoemployee";
    }

    @GetMapping("/loadholidaydeclarationform.do")
    public String getHolidayDeclarationForm(Model model)
    {
        List<EmployeeCatagory> employeeCatagories = employeeCatagoryService.getAllEmployeeCatagories();
        model.addAttribute("catagorylist",employeeCatagories);
        return "declareholiday";
    }

    @GetMapping("/loademployeeleavereport.do")
    public String getEmployeeMonthlyReport()
    {
        return "employeemonthlyreport";
    }

    @GetMapping("/loadadvemploysearch.do")
    public String getAdvEmployeeSearch(Model model)
    {
      List<Department> departments = employeeService.getAllDepartments();
      model.addAttribute("departments",departments);
      return "employeesearch";

    }

    @PostMapping("/advancesearchemployee.do")
    public String advancedSearchResult(@RequestParam(value = "empid",required = false)String empid,
                                       @RequestParam(value = "deptid")int deptid,
                                       @RequestParam("searchall")Integer searchall,Model model)
    {
        Employee employee = employeeService.getEmployeeById(empid.toUpperCase());

        if(employee == null)
        {
            if(searchall == 0)
            {
                List<Employee> employeeList = employeeService.getAllEmployee().stream().filter(employee1 -> {
                    return employee1.getEmployeename().toUpperCase().contains(empid.toUpperCase())
                            && (employee1.getStatus() == null || employee1.getStatus().equals(EmployeeStatus.ACTIVE) || employee1.getStatus().equals(EmployeeStatus.NEW));
                }).collect(Collectors.toList());
                model.addAttribute("employeelist",employeeList);
            }
            /*else
                if(employee.getStatus() != null && employee.getStatus().equals(EmployeeStatus.TERMINATED))
            {
                model.addAttribute("message","Employee Not Exist");
                return "employeenotexist";
            }*/
            else
            {
                List<Employee> employeeList = employeeService.getAllEmployee().stream().filter(employee1 -> {
                    return employee1.getEmployeename().toUpperCase().contains(empid.toUpperCase()) && employee1.getDepartment().getDepartmentid() == deptid
                            && (employee1.getStatus() == null || employee1.getStatus().equals(EmployeeStatus.ACTIVE) || employee1.getStatus().equals(EmployeeStatus.NEW));
                }).collect(Collectors.toList());
                model.addAttribute("employeelist",employeeList);
            }

        }
        else
        {
            if(employee.getStatus() != null && employee.getStatus().equals(EmployeeStatus.TERMINATED))
            {
                model.addAttribute("message","Employee Not Exist");
                return "employeenotexist";
            }
            else
            {
                List<Employee> employeeList = new ArrayList<>();
                employeeList.add(employee);
                model.addAttribute("employeelist",employeeList);
            }

        }
        return "showadvsearchresult";
    }

    @GetMapping("/loademployeeleavebalanceapplication.do")
    public String getEmployeeLeaveBalanceForm()
    {
        return "searchforbalance";
    }

    @PostMapping("/fetchemployeeleavebalanceapplication.do")
    public String getEmployeeLeaveBalance(@RequestParam("empid")String empid,Model model)
    {
        Employee employee = employeeService.getEmployeeById(empid.toUpperCase());
        if((employee.getStatus() != null && employee.getStatus().equals(EmployeeStatus.TERMINATED)) || (employee.getStatus() != null && employee.getStatus().equals(EmployeeStatus.HALF_TERMINATED))
                )
        {
            model.addAttribute("message","Employee Not Exist");
            return "employeenotexist";
        }
        else
            if(employee.getStatus().equals(EmployeeStatus.HALF_TERMINATED))
            {
                model.addAttribute("WARNING","The employee is terminated");
            }
        model.addAttribute("employee",employee);
        return "showemployeebalance";
    }


    @PostMapping("/fetchadvsearchcriteria.do")
    public String openAdvLeaveAppCriteriaForm(@RequestParam("empid")String empid,Model model)
    {
        model.addAttribute("employeeid",empid.toUpperCase());
       return "advanceleaveapplicationcriteria";
    }


    @GetMapping("/loadadvemployeeleaveapplication.do")
    public String openAdvLeaveAppCriteriaSearch(Model model)
    {

        return "advanceleaveemployeesearch";
    }


    @GetMapping("/loadrhdeclarationform.do")
    public String openRHDeclarationForm(Model model)
    {
        List<EmployeeCatagory> employeeCatagories = employeeCatagoryService.getAllEmployeeCatagories();
        model.addAttribute("catagorylist",employeeCatagories);
        return "declarerh";
    }

    @GetMapping("/loademployeedelete.do")
    public String getEmployeeDeleteSearch(Model mode)
    {
        return "deleteemployeesearch";
    }

    @PostMapping("/fetchdeletesearchcriteria.do")
    public String fetchDeleteEmployeeForm(@RequestParam("empid")String empid,Model model)
    {
        Employee employee = employeeService.getEmployeeById(empid.toUpperCase());
        if (employee.getStatus() == null || employee.getStatus().equals(EmployeeStatus.ACTIVE)) {
            model.addAttribute("employee", employee);
            return "employeedeleteform";
        }
        else
        {
            model.addAttribute("message","Employee Not Exist");
            return "employeenotexist";
        }
    }

    @GetMapping("/loadchangepasswordform.do")
    public String getChangePasswordForm()
    {
        return "changepassword";
    }

    @GetMapping("/loadunrestrictedlwpapplyform.do")
    public String getUnrestrictedLWPApplyForm()
    {
        return "unrestrictedlwpform";
    }


    @PostMapping("/calculatelistofdates.do")
    public String calculateListOfDates(@RequestParam("fromdate")String fromdate,@RequestParam("todate")String todate,Model model)
    {
        List<String> dates = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate from = LocalDate.parse(fromdate,dateTimeFormatter);
        LocalDate to = LocalDate.parse(todate,dateTimeFormatter);
        long noofdays= ChronoUnit.DAYS.between(from,to);

        for(long i=0;i<=noofdays;i++)
        {
            LocalDate nextDate = from.plusDays(i);
            String ndate=nextDate.format(dateTimeFormatter);
            dates.add(ndate);
        }
        model.addAttribute("dates",dates);
        return "leavedateproperty";
    }


    @GetMapping("/schedule1stjan.do")
    public String authenticateFor1stJanSchedule(Model model)
    {
        model.addAttribute("user",new User());
         return "loginfor1stjan";
    }

    @GetMapping("/schedule1stjuly.do")
    public String authenticateFor1stJulySchedule(Model model)
    {
        model.addAttribute("user",new User());
        return "loginfor1stjuly";
    }


    @GetMapping("/loademployeeleavehistoryreport.do")
    public String getEmployeeLeaveHistory()
    {
        return "employeehistoryreport";
    }


    @GetMapping("/loadrevertedleavereport.do")
    public String getReveredReportPage(Model model)
    {
       model.addAttribute("departments",employeeService.getAllDepartments());
       return "revertedleavereport";
    }

    @PostMapping("/borrow.do")
    public String processBorrow(@RequestParam("empid")String empid,@RequestParam("leaveid")int leaveid,Model model)
    {
        float totalNextSemensterBalance=employeeService.findEmployeeNextSemesterBalance(empid,leaveid);
        List<Float> values = new ArrayList<>();
        for (float x=1; x<=totalNextSemensterBalance; x=x+0.5f)
        {
            values.add(x);
        }
        model.addAttribute("leavebalance",values);
        model.addAttribute("empid",empid);
        model.addAttribute("leaveid",leaveid);
        return "showborrowpage";
    }

}
