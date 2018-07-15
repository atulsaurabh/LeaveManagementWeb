package org.leavemanagement.service;

import org.leavemanagement.entity.*;
import org.leavemanagement.repository.*;
import org.leavemanagement.status.DayStatus;
import org.leavemanagement.status.EmployeeStatus;
import org.leavemanagement.status.LeaveStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by atul_saurabh on 20/11/17.
 */

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService
{

    private float totalLeaveAvailableinNextSemester=0;
    private int from;
    private int to;
    private int year;

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private LeaveCatagoryRepository leaveCatagoryRepository;

    @Autowired
    private TerminatedEmployeeRepository terminatedEmployeeRepository;

    @Autowired
    public UserRepository userRepository;

    @Override
    public boolean addEmployee(Employee employee)
    {
        Employee emp = employeeRepository.saveAndFlush(employee);
        return emp != null;
    }

    @Override
    public boolean addDepartment(Department department) {
        Department dep = departmentRepository.save(department);
        return dep != null;
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public Department getDepartmentFromId(int id) {
        return departmentRepository.findOne(id);
    }

    @Override
    public Employee getEmployeeById(String id) {
        return employeeRepository.findOne(id);
    }

    @Override
    public List<Employee> getAllEmployee() {
        return employeeRepository.findAll();
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        Employee e= employeeRepository.saveAndFlush(employee);
        return e != null;
    }

    @Override
    public boolean isLeaveAppliedInSpecifiedPeriod(Employee employee, String startdate,String endDate) {
        Predicate<LeaveApplication> leaveApplicationPredicate = leaveApplication -> {
            return leaveApplication.getFromDate().equals(startdate) && leaveApplication.getToDate().equals(endDate);
        };

        return employee.getLeaveApplications().stream().anyMatch(leaveApplicationPredicate);
    }


    @Override
    public boolean isDateInRange(Employee employee, LocalDate fromDate, LocalDate toDate,boolean isfirsthalf) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if(ChronoUnit.DAYS.between(fromDate,toDate) ==0)
        {
               Predicate<LeaveApplication> leaveApplicationPredicate = leaveApplication -> {
                   return leaveApplication.getApplicationDays().stream().anyMatch(applicationDays -> {
                       return applicationDays.getAppliedDate().equals(fromDate.format(formatter)) && applicationDays.getStatus().equals(DayStatus.DAY_ACTIVE);
                   });
               };

               if(employee.getLeaveApplications().stream().filter(leaveApplicationPredicate).count() == 2)
                   return true;
               else
               {
                   long countd = employee.getLeaveApplications().stream().filter(leaveApplicationPredicate).count();
                   if(countd != 0) {
                       LeaveApplication leaveApplication = employee.getLeaveApplications().stream().filter(leaveApplicationPredicate).findFirst().get();
                       Predicate<ApplicationDays> applicationDaysPredicate = applicationDays -> {
                           return applicationDays.getAppliedDate().equals(fromDate.format(formatter));
                       };

                       ApplicationDays applicationDays = leaveApplication.getApplicationDays().stream().filter(applicationDaysPredicate).findFirst().get();

                       if (!applicationDays.isHalfday())
                           return true;
                       else
                           if(applicationDays.isFirsthalf() == isfirsthalf)
                           return true;
                       else
                           return false;
                   }
                   else
                       return false;

               }
        }
        else
        {
            Predicate<LeaveApplication> leaveApplicationPredicate = leaveApplication -> {

                LocalDate from_date = LocalDate.parse(leaveApplication.getFromDate(),formatter);
                return leaveApplication.getApplicationDays().parallelStream().anyMatch(applicationDays -> {
                    LocalDate appliedDate = LocalDate.parse(applicationDays.getAppliedDate(),formatter);
                    return (appliedDate.isEqual(fromDate) || (appliedDate.isAfter(fromDate) && appliedDate.isBefore(toDate))) &&
                            (applicationDays.getStatus().equals(DayStatus.DAY_ACTIVE));
                });
            };
            return employee.getLeaveApplications().stream().anyMatch(leaveApplicationPredicate);
        }

    }

    @Override
    public List<LeaveApplication> getLast10LeaveApplication(String empid,int fromDate,int toDate,int year)
    {

        Employee employee = employeeRepository.findOne(empid);

         List<LeaveApplication> leaveapps=employee.getLeaveApplications().stream().sorted((o1, o2) -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
          LocalDate firstDate = LocalDate.parse(o1.getFromDate(),formatter);
          LocalDate secondDate = LocalDate.parse(o2.getFromDate(),formatter);
          return firstDate.compareTo(secondDate);
        }).filter(leaveApplication -> {
            return leaveApplication.getApplicationDays().parallelStream().anyMatch(applicationDays -> {
                int appmonth = Integer.parseInt(applicationDays.getAppliedDate().split("/")[1]);
                int appyear = Integer.parseInt(applicationDays.getAppliedDate().split("/")[2]);
                return  applicationDays.getStatus().equals(DayStatus.DAY_ACTIVE)
                        &&
                        appmonth >= fromDate && appmonth <= toDate
                        &&
                        appyear == year
                        ;
            });
         }).collect(Collectors.toList());
        Collections.reverse(leaveapps);
        return leaveapps.stream().limit(10).collect(Collectors.toList());
    }


    @Override
    public List<LeaveCatagory> getAllEmployeeLeaves() {

        return leaveCatagoryRepository.findAll();
    }

    @Override
    public boolean addTerminatedEmployee(TerminatedEmployee terminatedEmployee) {
        TerminatedEmployee terminatedEmployee1 = terminatedEmployeeRepository.save(terminatedEmployee);
        return terminatedEmployee1 != null;
    }


    @Override
    public boolean deleteEmployee(TerminatedEmployee employee) {
        return terminatedEmployeeRepository.saveAndFlush(employee) != null;
    }

    @Override
    public List<TerminatedEmployee> getAllterminatedEmployee() {

        return terminatedEmployeeRepository.findAll();
    }

    @Override
    public boolean changeUserPassword(String newpassword) {
        User user = userRepository.findAll().stream().filter(user1 -> {
            return user1.getRolename().equals("ADMIN");
        }).findFirst().get();
        user.setPassword(newpassword );
        return userRepository.saveAndFlush(user) != null;
    }

    @Override
    public boolean terminateEmployee(Employee employee)
    {
        employee.setStatus(EmployeeStatus.TERMINATED);
        return employeeRepository.saveAndFlush(employee) != null;
    }

    @Override
    public float findEmployeeNextSemesterBalance(String empid, int leaveid)
    {
        totalLeaveAvailableinNextSemester=0;

       Employee employee=employeeRepository.findOne(empid);
       EmployeeLeave empLeave=employee.getLeaves().stream().filter(employeeLeave -> {
           return employeeLeave.getLeaveid() == leaveid;
       }).findFirst().get();

       List<LeaveApplication> lapp=employee.getLeaveApplications().stream().filter(leaveApplication -> {
           int currentMonth=LocalDate.now().getMonthValue();
           from=0;
            to=0;
            year=0;
           if (currentMonth >=1 && currentMonth <=6) {
               from = 7;
               to= 12;
               year=LocalDate.now().getYear();
           }
           else
           {
               from=1;
               to=6;
               year=LocalDate.now().getYear()+1;
           }
           LocalDate fromDate = LocalDate.parse(leaveApplication.getFromDate(),DateTimeFormatter.ofPattern("dd/MM/yyyy"));
           LocalDate toDate = LocalDate.parse(leaveApplication.getToDate(),DateTimeFormatter.ofPattern("dd/MM/yyyy"));
           return (fromDate.getMonthValue() >= from && fromDate.getMonthValue() <= to) &&
                   (fromDate.getYear() == year && toDate.getYear() == year)
                   && leaveApplication.getLeavetype().getLeaveCatagory().getAbbriviation().equals(empLeave.getLeaveCatagory().getAbbriviation());
       }).collect(Collectors.toList());
       lapp.stream().forEach(leaveApplication -> {
           List<ApplicationDays> appdays=leaveApplication.getApplicationDays().stream().filter(applicationDays -> {
               LocalDate date=LocalDate.parse(applicationDays.getAppliedDate(),DateTimeFormatter.ofPattern("dd/MM/yyyy"));

               return (date.getMonthValue()>=from && date.getMonthValue() <= to) && (date.getYear() == year) && applicationDays.getStatus().equals(DayStatus.DAY_ACTIVE);
           }).collect(Collectors.toList());
           appdays.stream().forEach(applicationDays -> {
                  totalLeaveAvailableinNextSemester+= (applicationDays.isHalfday() ? 0.5 : 1.0);
           });
       });

        return empLeave.getLeaveCatagory().getCommonvalue()-totalLeaveAvailableinNextSemester;
    }


    @Override
    public boolean borrowLeaveAmountAndUpdate(String empid, int leaveid, float amount)
    {
        Employee employee = employeeRepository.getOne(empid);
        EmployeeLeave employeeLeave=employee.getLeaves().stream().filter(employeeLeave1 -> {
            return employeeLeave1.getLeaveid() == leaveid;
        }).findFirst().get();
        employeeLeave.setTotalApplicable(employeeLeave.getTotalApplicable()+amount);
        employeeLeave.setBalance(employeeLeave.getBalance()+amount);
        employeeLeave.setBorrow(amount);

        return employeeRepository.saveAndFlush(employee) !=null;
    }
}
