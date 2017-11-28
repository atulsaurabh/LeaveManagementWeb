package org.leavemanagement.dto;

import org.leavemanagement.entity.Employee;
import org.leavemanagement.entity.LeaveApplication;

public class EmployeeAndLeave
{
    private Employee employee;
    private LeaveApplication leaveApplication;
    private int leaveid;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LeaveApplication getLeaveApplication() {
        return leaveApplication;
    }

    public void setLeaveApplication(LeaveApplication leaveApplication) {
        this.leaveApplication = leaveApplication;
    }

    public int getLeaveid() {
        return leaveid;
    }

    public void setLeaveid(int leaveid) {
        this.leaveid = leaveid;
    }
}
