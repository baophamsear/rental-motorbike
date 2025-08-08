package com.pqb.motor_rental.services;

public interface EmailService {
    void sendVerificationCodeEmail(String toEmail, String code);
}
