package org.leavemanagement.service;

import org.leavemanagement.entity.User;
import org.leavemanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Predicate;

/**
 * Created by atul_saurabh on 19/11/17.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService
{
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean save(User user)
    {
        User u=userRepository.save(user);
        if(u != null)
            return true;
        else
            return false;
    }

    @Override
    public boolean userLogin(final String username, final String password) {
        Predicate<User> loginCheckerPredicate = user -> {
        return     user.getUsername().equals(username) && user.getPassword().equals(password) ;
        };
        return  userRepository.findAll().stream().anyMatch(loginCheckerPredicate);
    }
}
