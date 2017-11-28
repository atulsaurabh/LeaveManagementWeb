package org.leavemanagement.service;


import org.leavemanagement.entity.EmployeeLeave;
import org.leavemanagement.entity.LeaveCatagory;
import org.leavemanagement.repository.LeaveCatagoryRepository;
import org.leavemanagement.repository.LeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LeaveServiceImpl implements LeaveService
{
    @Autowired
    private LeaveCatagoryRepository leaveCatagoryRepository;
    @Autowired
    private LeaveRepository leaveRepository;

    @Override
    public boolean addLeaveCatagory(LeaveCatagory leaveCatagory)
    {
       LeaveCatagory leave = leaveCatagoryRepository.save(leaveCatagory);
       return leave != null;
    }


    @Override
    public List<LeaveCatagory> getAllLeaveCatagory()
    {
        List<LeaveCatagory> leaves = leaveCatagoryRepository.findAll();
        return leaves;
    }

    @Override
    public float getLeaveBalance(int leaveid) {
        EmployeeLeave leave=leaveRepository.findOne(leaveid);
        return leave.getBalance();
    }

    @Override
    public EmployeeLeave getLeaveById(int leaveid) {
        return leaveRepository.findOne(leaveid);
    }
}
