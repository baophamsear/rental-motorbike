package com.pqb.motor_rental.services.impl;

import com.pqb.motor_rental.entities.User;
import com.pqb.motor_rental.enums.AccountStatus;
import com.pqb.motor_rental.repositories.UserRepository;
import com.pqb.motor_rental.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    // Dòng này không khuyến khích sử dụng để dùng, tránh trường hợp Inject sai cách
//    private UserRepository userRepository;

    // Cách mới dùng "constructor-based dependency injection (DI)":
    // inject đúng và đủ khi Spring khởi tạo UserServiceImpl
    // Tránh lỗi NullPointerException
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByUserId(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    // Dòng này không được kế thừa từ cha của nó
    @Override
    public void register(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAccountStatus(AccountStatus.normal);
        userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Integer userId) {
        return userRepository.findById(userId);
    }
}
