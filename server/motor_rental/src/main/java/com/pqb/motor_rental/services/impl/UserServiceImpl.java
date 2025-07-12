package com.pqb.motor_rental.services.impl;

import com.pqb.motor_rental.entities.User;
import com.pqb.motor_rental.repositories.UserRepository;
import com.pqb.motor_rental.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;


    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByUserId(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
