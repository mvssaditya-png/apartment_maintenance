package com.apartment.maintenance.dto;

import java.util.UUID;

public class AdminUserResponse {

    private UUID userId;
    private UUID siteId;
    private UUID flatId;

    private String flatNumber;
    private String name;
    private String phoneNumber;
    private String email;
    private String role;
    private Boolean isActive;

    public AdminUserResponse(
            UUID userId,
            UUID siteId,
            UUID flatId,
            String flatNumber,
            String name,
            String phoneNumber,
            String email,
            String role,
            Boolean isActive
    ) {
        this.userId = userId;
        this.siteId = siteId;
        this.flatId = flatId;
        this.flatNumber = flatNumber;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getSiteId() {
        return siteId;
    }

    public UUID getFlatId() {
        return flatId;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public Boolean getIsActive() {
        return isActive;
    }
}