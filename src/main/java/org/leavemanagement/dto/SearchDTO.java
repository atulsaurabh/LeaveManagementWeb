package org.leavemanagement.dto;

public class SearchDTO
{
    private String employeeid;
    private int includeall;

    public String getEmployeeid() {
        return employeeid;
    }

    public void setEmployeeid(String employeeid) {
        this.employeeid = employeeid;
    }

    public int getIncludeall() {
        return includeall;
    }

    public void setIncludeall(int includeall) {
        this.includeall = includeall;
    }
}
