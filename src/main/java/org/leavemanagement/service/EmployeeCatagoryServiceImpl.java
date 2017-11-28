package org.leavemanagement.service;

import org.leavemanagement.entity.Employee;
import org.leavemanagement.entity.EmployeeCatagory;
import org.leavemanagement.repository.EmployeeCatagoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class EmployeeCatagoryServiceImpl implements EmployeeCatagoryService
{
    @Autowired
    private EmployeeCatagoryRepository employeeCatagoryRepository;

    @Override
    public boolean addCatagory(EmployeeCatagory catagory)
    {
        EmployeeCatagory cat = employeeCatagoryRepository.save(catagory);
        return cat != null;
    }

    @Override
    public List<EmployeeCatagory> getAllEmployeeCatagories() {
        return employeeCatagoryRepository.findAll();
    }

    @Override
    public EmployeeCatagory getEmployeeCatagoryById(int id) {
        return employeeCatagoryRepository.findOne(id);
    }
}
