package org.leavemanagement.service;

import org.leavemanagement.entity.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Created by atul_saurabh on 20/11/17.
 */
public interface EmployeeService
{
    public boolean addEmployee(Employee employee);
    public boolean addDepartment(Department department);
    public List<Department> getAllDepartments();
    public Department getDepartmentFromId(int id);
    public Employee getEmployeeById(String id);
    public List<Employee> getAllEmployee();
    public boolean updateEmployee(Employee employee);
    public boolean isLeaveAppliedInSpecifiedPeriod(Employee employee,String startdate,String endDate);
    public boolean isDateInRange(Employee employee, LocalDate fromDate, LocalDate toDate,boolean isfirsthalf);
    public List<LeaveApplication> getLast10LeaveApplication(String empid,int fromDate,int toDate,int year);
    public List<LeaveCatagory> getAllEmployeeLeaves();

    public boolean addTerminatedEmployee(TerminatedEmployee terminatedEmployee);
    public boolean deleteEmployee(TerminatedEmployee employee);
    public boolean terminateEmployee(Employee employee);
    public List<TerminatedEmployee> getAllterminatedEmployee();
    public boolean changeUserPassword(String newpassword);
    public float findEmployeeNextSemesterBalance(String empid,int leaveid);
    public boolean borrowLeaveAmountAndUpdate(String empid,int leaveid,float amount);

}
