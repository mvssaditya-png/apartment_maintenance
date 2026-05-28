package com.apartment.maintenance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoggedInUserDTO {
    private UUID userId;
    private UUID siteId;
    private UUID flatId;
    private String siteName;
    private String flatNumber;
    private String email;
    private boolean isActive;
    private String name;
    private String phoneNumber;
    private String role;
    private LocalDateTime createdAt;
    private String residentType;
    private UUID ownerUserId;
    private String ownerName;
    private String ownerPhoneNumber;
}
