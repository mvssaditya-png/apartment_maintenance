package com.apartment.maintenance.dto;

import lombok.Data;

@Data
public class VerifySubscriptionPaymentRequest {

    private String razorpayOrderId;

    private String razorpayPaymentId;

    private String razorpaySignature;
}