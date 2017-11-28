package org.leavemanagement.repository;

import org.leavemanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by atul_saurabh on 20/11/17.
 */

public interface EmployeeRepository extends JpaRepository<Employee,String>
{
}
