package org.leavemanagement.entity;

import javax.persistence.*;

@Entity
public class LeaveApplication
{
    private int leaveapplicationid;
    private String reason;
    private float noofdays;
    private EmployeeLeave leavetype;
    private String fromDate;
    private String toDate;
    private boolean halfdayApplicable;
    private boolean firstDayAsHalfDay;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getLeaveapplicationid() {
        return leaveapplicationid;
    }

    public void setLeaveapplicationid(int leaveapplicationid) {
        this.leaveapplicationid = leaveapplicationid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public float getNoofdays() {
        return noofdays;
    }

    public void setNoofdays(float noofdays) {
        this.noofdays = noofdays;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "leaveapp_leavetype")
    public EmployeeLeave getLeavetype() {
        return leavetype;
    }

    public void setLeavetype(EmployeeLeave leavetype) {
        this.leavetype = leavetype;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public boolean isHalfdayApplicable() {
        return halfdayApplicable;
    }

    public void setHalfdayApplicable(boolean halfdayApplicable) {
        this.halfdayApplicable = halfdayApplicable;
    }

    public boolean isFirstDayAsHalfDay() {
        return firstDayAsHalfDay;
    }

    public void setFirstDayAsHalfDay(boolean firstDayAsHalfDay) {
        this.firstDayAsHalfDay = firstDayAsHalfDay;
    }
}
