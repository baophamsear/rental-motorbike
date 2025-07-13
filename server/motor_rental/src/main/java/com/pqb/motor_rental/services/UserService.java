package com.pqb.motor_rental.services;

import com.pqb.motor_rental.entities.User;
import org.springframework.stereotype.Service;

import java.util.List;


public interface UserService {
    List<User> getAllUsers();
    User getUserByUserId(int userId);
    void register(User user);

}
