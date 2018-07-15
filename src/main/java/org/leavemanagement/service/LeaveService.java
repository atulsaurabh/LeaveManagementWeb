package org.leavemanagement.service;

import org.leavemanagement.entity.EmployeeLeave;
import org.leavemanagement.entity.LeaveCatagory;

import java.util.List;

public interface LeaveService {
    public boolean addLeaveCatagory(LeaveCatagory leaveCatagory);
    public List<LeaveCatagory> getAllLeaveCatagory();
    public float getLeaveBalance(int leaveid);
    public EmployeeLeave getLeaveById(int leaveid);
}
