package com.pqb.motor_rental.controllers;

import com.pqb.motor_rental.entities.User;
import com.pqb.motor_rental.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public String getUserProfile(@PathVariable Integer id, Model model) {
        User user = userService.getUserByUserId(id);
        model.addAttribute("user", user);
        return "profile"; // trả về file profile.html trong templates
    }
}
