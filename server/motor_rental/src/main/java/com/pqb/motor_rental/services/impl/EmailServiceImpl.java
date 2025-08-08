package com.pqb.motor_rental.services.impl;

import com.pqb.motor_rental.services.EmailService;
import org.springframework.mail.SimpleMailMessage;

public class EmailServiceImpl implements EmailService {
    @Override
    public void sendVerificationCodeEmail(String toEmail, String code) {
        String subject = "Verify your email";
        String message = "Your verification code is: " + code + "\nValid for 5 minutes.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(toEmail);
        email.setSubject(subject);
        email.setText(message);
        email.setTo(String.valueOf(email));
    }
}
