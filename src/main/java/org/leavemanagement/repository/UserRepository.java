package org.leavemanagement.repository;

import org.leavemanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by atul_saurabh on 19/11/17.
 */
public interface UserRepository extends JpaRepository<User,Integer>
{

}
