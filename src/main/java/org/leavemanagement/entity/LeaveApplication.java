package org.leavemanagement.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class LeaveApplication implements Comparable
{
    private Employee employee;
    private int leaveapplicationid;
    private String reason;
    private float noofdays;
    private EmployeeLeave leavetype;
    private String fromDate;
    private String toDate;
    private boolean halfdayApplicable;
    private boolean firstDayAsHalfDay;
    private String applicationDate;
    private List<ApplicationDays> applicationDays = new ArrayList<>();
    private String status;


    private String reportFromDate;
    private String reportToDate;
    private String reportHalfDay;

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

    public String getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(String applicationDate) {
        this.applicationDate = applicationDate;
    }

  @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    @JoinTable(
            name = "leaveapp_applieddate",
            joinColumns = @JoinColumn(name = "leaveapplicationid"),
            inverseJoinColumns = @JoinColumn(name = "dateid")

    )

    public List<ApplicationDays> getApplicationDays() {
        return applicationDays;
    }

    public void setApplicationDays(List<ApplicationDays> applicationDays) {
        this.applicationDays = applicationDays;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_leaveapp")
    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @Transient
    public String getReportFromDate() {
        return reportFromDate;
    }

    public void setReportFromDate(String reportFromDate) {
        this.reportFromDate = reportFromDate;
    }

    @Transient
    public String getReportToDate() {
        return reportToDate;
    }

    public void setReportToDate(String reportToDate) {
        this.reportToDate = reportToDate;
    }


    @Transient
    public String getReportHalfDay() {
        return reportHalfDay;
    }

    public void setReportHalfDay(String reportHalfDay) {
        this.reportHalfDay = reportHalfDay;
    }

    @Override
    public int compareTo(Object o)
    {
        return 0;
    }
}
