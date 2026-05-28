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
    private String residentType;
    private UUID ownerUserId;
    private String ownerName;
    private String ownerPhoneNumber;
    public AdminUserResponse(
            UUID userId,
            UUID siteId,
            UUID flatId,
            String flatNumber,
            String name,
            String phoneNumber,
            String email,
            String role,
            Boolean isActive,
            String residentType,
            UUID ownerUserId,
            String ownerName,
            String ownerPhoneNumber
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
        this.residentType = residentType;
        this.ownerName = ownerName;
        this.ownerUserId = ownerUserId;
        this.ownerPhoneNumber = ownerPhoneNumber;
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

    public String getResidentType() {
        return residentType;
    }

    public void setResidentType(String residentType) {
        this.residentType = residentType;
    }

    public UUID getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(UUID ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPhoneNumber() {
        return ownerPhoneNumber;
    }

    public void setOwnerPhoneNumber(String ownerPhoneNumber) {
        this.ownerPhoneNumber = ownerPhoneNumber;
    }
}