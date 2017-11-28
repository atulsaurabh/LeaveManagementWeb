package org.leavemanagement.service;

import org.leavemanagement.entity.EmployeeCatagory;

import java.util.List;

public interface EmployeeCatagoryService
{
    public boolean addCatagory(EmployeeCatagory catagory);
    public List<EmployeeCatagory> getAllEmployeeCatagories();
    public EmployeeCatagory getEmployeeCatagoryById(int id);
}
