package com.pqb.motor_rental.entities;

import com.pqb.motor_rental.enums.AccountStatus;
import com.pqb.motor_rental.enums.Role;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    private String fullName;
    private String email;
    private String phone;
    private String password;


    @Enumerated(EnumType.STRING)
    private Role role;

    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private String violationNote;

    private LocalDateTime limitedUntil;

    private Boolean isVerified = false;

    private String verificationCode;

    private LocalDateTime codeExpiry;
    private String pushToken;

    public User() {}

    public User(Integer userId, String fullName, String email, String phone, String password, Role role,
                String avatarUrl, AccountStatus accountStatus, LocalDateTime createdAt, String violationNote,
                LocalDateTime limitedUntil, Boolean isVerified, String verificationCode, LocalDateTime codeExpiry,
                String pushToken) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.avatarUrl = avatarUrl;
        this.accountStatus = accountStatus;
        this.createdAt = createdAt;
        this.violationNote = violationNote;
        this.limitedUntil = limitedUntil;
        this.isVerified = isVerified;
        this.verificationCode = verificationCode;
        this.codeExpiry = codeExpiry;
        this.pushToken = pushToken;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getViolationNote() {
        return violationNote;
    }

    public void setViolationNote(String violationNote) {
        this.violationNote = violationNote;
    }

    public LocalDateTime getLimitedUntil() {
        return limitedUntil;
    }

    public void setLimitedUntil(LocalDateTime limitedUntil) {
        this.limitedUntil = limitedUntil;
    }

    public Boolean getVerified() {
        return isVerified;
    }

    public void setVerified(Boolean verified) {
        isVerified = verified;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public LocalDateTime getCodeExpiry() {
        return codeExpiry;
    }

    public void setCodeExpiry(LocalDateTime codeExpiry) {
        this.codeExpiry = codeExpiry;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }
}
