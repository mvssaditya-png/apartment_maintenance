package com.apartment.maintenance.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateSiteRequest {

    private String siteName;
    private String address;
    private String city;
    private String state;

    private BigDecimal maintenanceAmount;
    private BigDecimal openingBalance;
    private Integer totalFlats;

    private String adminName;
    private String adminPhoneNumber;
    private String adminEmail;
}