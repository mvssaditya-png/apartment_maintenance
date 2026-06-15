package com.apartment.maintenance.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuperAdminDashboardResponse {

    private Long totalSites;

    private Long trialSites;

    private Long activeSites;

    private Long expiredSites;

    private Long inactiveSites;
}