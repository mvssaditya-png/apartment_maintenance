package com.apartment.maintenance.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class CreateSubscriptionOrderResponse {

    private UUID subscriptionId;

    private UUID planId;

    private String razorpayOrderId;

    private BigDecimal amount;

    private Integer amountInPaise;

    private String currency;

    private String keyId;
}