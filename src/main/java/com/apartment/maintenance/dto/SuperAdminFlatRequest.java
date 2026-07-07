package com.apartment.maintenance.dto;

import lombok.Data;

@Data
public class SuperAdminFlatRequest {

    private String flatNumber;
    private String ownerName;
    private String ownerPhone;
    private String ownerEmail;
    private String role; // ADMIN, CASHIER, RESIDENT
}