package com.apartment.maintenance.dto;

import lombok.Data;

@Data
public class UpdateEmergencyContactRequest {
    private String name;
    private String role;
    private String phoneNumber;
    private Integer priority;
    private Boolean active;
}