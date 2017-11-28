package org.leavemanagement.dto;

public class EmployeeDTO {

    private String employeeid;
    private String employeename;
    private String dateofjoing;
    private int employeecatagoryid;
    private int departmentid;
    private float [] allowednoleaves;

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

    public String getEmployeeid() {
        return employeeid;
    }

    public void setEmployeeid(String employeeid) {
        this.employeeid = employeeid;
    }

    public float[] getAllowednoleaves() {
        return allowednoleaves;
    }

    public void setAllowednoleaves(float[] allowednoleaves) {
        this.allowednoleaves = allowednoleaves;
    }

}
