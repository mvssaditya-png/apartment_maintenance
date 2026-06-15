package com.apartment.maintenance.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubscriptionPlanRequest {

    private Integer minFlats;

    private Integer maxFlats;

    private Integer durationMonths;

    private BigDecimal amount;

    private Boolean active;
}