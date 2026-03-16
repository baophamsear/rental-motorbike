package com.pqb.motor_rental.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class IndexController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Xin chào từ Spring Boot!");
        return "index";  // trỏ đến templates/index.html
    }
}
