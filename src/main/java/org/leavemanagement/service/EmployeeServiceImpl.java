package org.leavemanagement.service;

import org.leavemanagement.entity.Department;
import org.leavemanagement.entity.Employee;
import org.leavemanagement.entity.LeaveApplication;
import org.leavemanagement.repository.DepartmentRepository;
import org.leavemanagement.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by atul_saurabh on 20/11/17.
 */

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService
{

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public boolean addEmployee(Employee employee)
    {
        Employee emp = employeeRepository.save(employee);
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
    public boolean isLeaveAppliedInSpecifiedPeriod(Employee employee, String startdate) {
        Predicate<LeaveApplication> leaveApplicationPredicate = leaveApplication -> {
            return leaveApplication.getFromDate().equals(startdate);
        };

        return employee.getLeaveApplications().stream().anyMatch(leaveApplicationPredicate);
    }
}
