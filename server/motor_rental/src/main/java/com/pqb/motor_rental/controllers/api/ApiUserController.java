package com.pqb.motor_rental.controllers.api;

import com.pqb.motor_rental.entities.User;
import com.pqb.motor_rental.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/users")
public class ApiUserController {

    private final UserService userService;

    public ApiUserController(UserService userService) {
        this.userService = userService;
    }

//    @GetMapping("/{id}")
//    public String getUserProfile(@PathVariable Integer id, Model model) {
//        User user = userService.getUserByUserId(id);
//        model.addAttribute("user", user);
//        return "profile"; // trả về file profile.html trong templates
//    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(Model model) {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
