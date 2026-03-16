package com.pqb.motor_rental.dto;

// Đăng nhập trả về
public class AuthenticationResponse {
    private String token;

    public AuthenticationResponse() {}

    public AuthenticationResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
