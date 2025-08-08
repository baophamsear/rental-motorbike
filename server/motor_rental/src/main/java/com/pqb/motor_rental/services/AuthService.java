package com.pqb.motor_rental.services;

import com.pqb.motor_rental.entities.User;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    void register(User user);
    void verifyCode(String email, String code);
}
