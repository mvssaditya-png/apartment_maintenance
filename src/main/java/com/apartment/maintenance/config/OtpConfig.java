package com.apartment.maintenance.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "otp")
public class OtpConfig {

    /**
     * OTP validity duration.
     */
    private int expiryMinutes;

    /**
     * Maximum OTP requests allowed within the request window.
     */
    private int maxRequests;

    /**
     * Time window (minutes) used to calculate OTP request limit.
     */
    private int requestWindowMinutes;
}