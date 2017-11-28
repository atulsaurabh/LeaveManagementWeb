package org.leavemanagement.service;

import org.leavemanagement.entity.User;

/**
 * Created by atul_saurabh on 19/11/17.
 */
public interface UserService
{
    public boolean save(User user);
    public boolean userLogin(String username,String password);
}
