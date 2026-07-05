package com.apartment.maintenance.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "otp_verifications")
public class OtpVerification {

    @Id
    @Column(name = "otp_id")
    private UUID otpId;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String otp;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    private Boolean verified = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}