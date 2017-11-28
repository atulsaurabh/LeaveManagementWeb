package org.leavemanagement.service;

import org.leavemanagement.entity.Department;
import org.leavemanagement.entity.Employee;

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
    public boolean isLeaveAppliedInSpecifiedPeriod(Employee employee,String startdate);
}
