package com.apartment.maintenance.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class SubscriptionStatusResponse {

    private String status;

    private Boolean allowed;

    private Boolean trial;

    private Boolean expired;

    private Long daysRemaining;

    private LocalDate trialEndDate;

    private LocalDate subscriptionEndDate;

    private String message;
}