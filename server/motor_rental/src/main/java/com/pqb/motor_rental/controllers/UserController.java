package com.pqb.motor_rental.controllers;

import com.pqb.motor_rental.entities.User;
import com.pqb.motor_rental.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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

    @GetMapping
    public String getUsers(Model model) {
        List<User> users = userService.getAllUsers(); // trả về danh sách bị suspended
        model.addAttribute("users", users);
        model.addAttribute("activePage", "users"); // sidebar active
        model.addAttribute("bodyContent", "user-list :: content"); // fragment to inject
        model.addAttribute("pageTitle", "User Management"); // title if needed
        return "layout";
    }
}
