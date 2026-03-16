package com.pqb.motor_rental.controllers.api;

import com.pqb.motor_rental.entities.User;
import com.pqb.motor_rental.repositories.UserRepository;
import com.pqb.motor_rental.security.CustomUserDetails;
import com.pqb.motor_rental.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/users")
public class ApiUserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public ApiUserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
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

    @PostMapping("/save-push-token")
    public ResponseEntity<?> savePushToken(@RequestBody Map<String, Object> request) {
        Integer userId = (Integer) request.get("userId");
        String token = (String) request.get("token");
        userRepository.findById(userId).ifPresent(user -> {
            user.setPushToken(token);
            userRepository.save(user);
        });
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('renter') or hasRole('lessor')") // hoặc role nào bạn muốn
    public ResponseEntity<User> getUserById(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // Lấy username/email hiện tại từ principal
        Integer userId = userDetails.getUser().getUserId();

        // Lấy user trong DB từ id
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(user);
    }
}
