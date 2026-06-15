package com.apartment.maintenance.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class SiteSummaryResponse {

    private UUID siteId;

    private String siteName;

    private String adminName;

    private String adminPhone;

    private Integer totalFlats;

    private String subscriptionStatus;

    private LocalDate trialEndDate;

    private LocalDate subscriptionEndDate;

    private Boolean active;
}