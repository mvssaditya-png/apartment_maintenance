package com.apartment.maintenance.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class SuperAdminFlatResponse {

    private UUID flatId;
    private UUID siteId;
    private String flatNumber;
    private String ownerName;
    private String ownerPhone;
    private String ownerEmail;
    private String role;
    private Boolean active;
}