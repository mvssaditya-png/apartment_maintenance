package com.apartment.maintenance.dto;

import java.util.UUID;

public class VerifyOtpResponse {

    private String token;
    private UUID userId;
    private String message;

    public VerifyOtpResponse(String token, UUID userId, String message) {
        this.token = token;
        this.userId = userId;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }
}