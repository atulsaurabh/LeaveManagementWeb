package org.leavemanagement.dto;

import org.leavemanagement.entity.EmployeeLeave;

import java.util.List;

public class EmployeeUpdateDTO
{
    private String employeeid;
    private String oldemployeeid;
    private String employeename;
    private String dateofjoing;
    private int employeecatagoryid;
    private int departmentid;
    private List<EmployeeLeave> leaves;

    private float [] allowednoleaves;


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

    public int getEmployeecatagoryid() {
        return employeecatagoryid;
    }

    public void setEmployeecatagoryid(int employeecatagoryid) {
        this.employeecatagoryid = employeecatagoryid;
    }

    public int getDepartmentid() {
        return departmentid;
    }

    public void setDepartmentid(int departmentid) {
        this.departmentid = departmentid;
    }

    public List<EmployeeLeave> getLeaves() {
        return leaves;
    }

    public void setLeaves(List<EmployeeLeave> leaves) {
        this.leaves = leaves;
    }

    public float[] getAllowednoleaves() {
        return allowednoleaves;
    }

    public void setAllowednoleaves(float[] allowednoleaves) {
        this.allowednoleaves = allowednoleaves;
    }

    public String getOldemployeeid() {
        return oldemployeeid;
    }

    public void setOldemployeeid(String oldemployeeid) {
        this.oldemployeeid = oldemployeeid;
    }
}
