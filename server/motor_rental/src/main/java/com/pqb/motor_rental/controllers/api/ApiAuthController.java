package com.pqb.motor_rental.controllers.api;

import com.pqb.motor_rental.dto.AuthenticationRequest;
import com.pqb.motor_rental.dto.AuthenticationResponse;
import com.pqb.motor_rental.entities.User;
import com.pqb.motor_rental.security.CustomUserDetails;
import com.pqb.motor_rental.security.CustomUserDetailsService;
import com.pqb.motor_rental.services.AuthService;
import com.pqb.motor_rental.services.UserService;
import com.pqb.motor_rental.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {
    private final AuthService authService;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public ApiAuthController(AuthService authService, AuthenticationManager authManager,
                             JwtUtil jwtUtil,
                             CustomUserDetailsService userDetailsService) {
        this.authService = authService;
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user){
        try{
            authService.register(user);
            return ResponseEntity.ok("User registered successfully");
        }catch (RuntimeException ex){
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
}
