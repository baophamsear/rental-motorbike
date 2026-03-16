package com.pqb.motor_rental.controllers.api;

import com.pqb.motor_rental.dto.AuthenticationRequest;
import com.pqb.motor_rental.dto.AuthenticationResponse;
import com.pqb.motor_rental.dto.VerificationRequest;
import com.pqb.motor_rental.entities.User;
import com.pqb.motor_rental.enums.Role;
import com.pqb.motor_rental.repositories.UserRepository;
import com.pqb.motor_rental.security.CustomUserDetails;
import com.pqb.motor_rental.security.CustomUserDetailsService;
import com.pqb.motor_rental.services.AuthService;
import com.pqb.motor_rental.services.CloudinaryService;
import com.pqb.motor_rental.services.UserService;
import com.pqb.motor_rental.services.impl.AuthServiceImpl;
import com.pqb.motor_rental.services.impl.EmailServiceImpl;
import com.pqb.motor_rental.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {
    private final AuthServiceImpl authServiceImpl;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final CloudinaryService cloudinaryService;
    private final EmailServiceImpl emailServiceImpl;
    private final UserRepository userRepository;

    public ApiAuthController(AuthService authService, AuthServiceImpl authServiceImpl, AuthenticationManager authManager,
                             JwtUtil jwtUtil,
                             CustomUserDetailsService userDetailsService, CloudinaryService cloudinaryService, EmailServiceImpl emailServiceImpl, UserRepository userRepository) {
        this.authServiceImpl = authServiceImpl;
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.cloudinaryService = cloudinaryService;
        this.emailServiceImpl = emailServiceImpl;
        this.userRepository = userRepository;
    }


    // Đăng kí người dùng app
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> register(
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("role") String role,
            @RequestParam("avatarUrl") MultipartFile avatarFile
    ){
        try{
            // Upload anh len cloudinary
            String imageUrl = cloudinaryService.uploadImage(avatarFile);
            User user = new User();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPassword(password);
            user.setRole(Role.valueOf(role.toLowerCase()));
            user.setAvatarUrl(imageUrl);

            authServiceImpl.register(user);
            return ResponseEntity.ok("User registered successfully");
        }catch (RuntimeException | IOException ex) {
           return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request){
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            String jwt = jwtUtil.generateToken(userDetails);
            return ResponseEntity.ok(new AuthenticationResponse(jwt));
        }catch (BadCredentialsException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    // Xác thực email code
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody VerificationRequest request){
        authServiceImpl.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok("Email verified successfully");
    }

    @GetMapping("/send-code")
    public ResponseEntity<?> sendVerificationEmail(@RequestParam("email") String email){
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String code = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expirationDate = LocalDateTime.now().plusMinutes(5);

        u.setVerificationCode(code);
        u.setCodeExpiry(expirationDate);
        u.setVerified(false);
        userRepository.save(u);

        emailServiceImpl.sendVerificationCodeEmail(email, code);
        return ResponseEntity.ok("Verification code sent successfully");
    }
}
