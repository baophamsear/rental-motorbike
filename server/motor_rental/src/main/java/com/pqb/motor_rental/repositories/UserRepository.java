package com.pqb.motor_rental.repositories;

import com.pqb.motor_rental.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User getUserByUserId(int userId);
}
