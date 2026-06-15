package com.apartment.maintenance.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class SubscriptionPlanResponse {

    private UUID planId;

    private Integer minFlats;

    private Integer maxFlats;

    private Integer durationMonths;

    private BigDecimal amount;

    private String label;
}