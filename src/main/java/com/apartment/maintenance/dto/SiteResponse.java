package com.apartment.maintenance.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class SiteResponse {

    private UUID siteId;

    private String siteName;

    private Integer totalFlats;

    private String subscriptionStatus;

    private LocalDate trialStartDate;

    private LocalDate trialEndDate;

    private Boolean isActive;
}