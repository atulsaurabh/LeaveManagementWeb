package org.leavemanagement.entity;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by atul_saurabh on 20/11/17.
 */

@Entity
public class Employee
{
    private String employeeid;
    private String employeename;
    private String dateofjoing;
    private EmployeeCatagory catagory;
    private Department department;
    private List<EmployeeLeave> leaves = new ArrayList<>();
    private List<LeaveApplication> leaveApplications = new ArrayList<>();
    private String status;
    @Id
    public String getEmployeeid() {
        return employeeid;
    }

    public void setEmployeeid(String employeeid) {
        this.employeeid = employeeid;
    }

    public String getEmployeename() {
        return employeename;
    }

    public void setEmployeename(String employeename) {
        this.employeename = employeename;
    }

    public String getDateofjoing() {
        return dateofjoing;
    }

    public void setDateofjoing(String dateofjoing) {
        this.dateofjoing = dateofjoing;
    }


    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "emp_cat")
    @Cascade(org.hibernate.annotations.CascadeType.REMOVE)
    public EmployeeCatagory getCatagory() {
        return catagory;
    }

    public void setCatagory(EmployeeCatagory catagory) {
        this.catagory = catagory;
    }

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "emp_dept")
    @Cascade(org.hibernate.annotations.CascadeType.REMOVE)
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    @JoinTable(
            joinColumns = @JoinColumn(name = "employeeid"),
            inverseJoinColumns = @JoinColumn(name = "leaveid")
    )
    @Cascade(org.hibernate.annotations.CascadeType.REMOVE)
    public List<EmployeeLeave> getLeaves() {
        return leaves;
    }


    public void setLeaves(List<EmployeeLeave> leaves) {
        this.leaves = leaves;
    }


    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true,fetch =FetchType.EAGER)
    @JoinTable(
            joinColumns = @JoinColumn(name = "employeeid"),
            inverseJoinColumns = @JoinColumn(name = "leaveapplicationid")
    )
    @Cascade({org.hibernate.annotations.CascadeType.REMOVE})
    public List<LeaveApplication> getLeaveApplications() {
        return leaveApplications;
    }

    public void setLeaveApplications(List<LeaveApplication> leaveApplications) {
        this.leaveApplications = leaveApplications;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
