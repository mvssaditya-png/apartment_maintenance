package com.apartment.maintenance.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateSubscriptionOrderRequest {

    private UUID planId;
}