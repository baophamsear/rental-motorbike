package com.pqb.motor_rental.services.impl;

import com.pqb.motor_rental.entities.User;
import com.pqb.motor_rental.enums.Role;
import com.pqb.motor_rental.repositories.UserRepository;
import com.pqb.motor_rental.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public void register(User user) {
        if(userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("Email already exists");
        }

        // Tạo mã xác thực
        String code = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expirationDate = LocalDateTime.now().plusMinutes(5);

        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setFullName(user.getFullName());
        // Băm mật khẩu
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setRole(user.getRole());
        newUser.setAvatarUrl(user.getAvatarUrl());

        // Xác thực email
        newUser.setVerified(false);
        newUser.setVerificationCode(code);
        newUser.setCodeExpiry(expirationDate);

        userRepository.save(newUser);


    }

    @Override
    public void verifyCode(String email, String code) {
        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new RuntimeException("User not found"));

        if(user.getVerified()){
            throw new RuntimeException("User already verified");
        }

        if(!code.equals(user.getVerificationCode())){
            throw new RuntimeException("Incorrect verification code");
        }

        if (user.getCodeExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("User code expiry");
        }

        // nếu còn hạn và đúng
        user.setVerified(true);
        user.setVerificationCode(null);
        user.setCodeExpiry(null);
        userRepository.save(user);
    }
}
