package com.apartment.maintenance.dto;

import lombok.Data;

@Data
public class CreateEmergencyContactRequest {
    private String name;
    private String role;
    private String phoneNumber;
    private Integer priority;
    private Boolean active;
}