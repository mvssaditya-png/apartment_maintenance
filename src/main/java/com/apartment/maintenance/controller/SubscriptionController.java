package com.apartment.maintenance.controller;

import com.apartment.maintenance.dto.*;
import com.apartment.maintenance.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/status")
    public SubscriptionStatusResponse getStatus(
            @AuthenticationPrincipal UUID userId
    ) {
        return subscriptionService.getStatus(userId);
    }

    @GetMapping("/plans")
    public List<SubscriptionPlanResponse> getPlans(
            @AuthenticationPrincipal UUID userId
    ) {
        return subscriptionService.getPlans(userId);
    }

    @PostMapping("/create-order")
    public CreateSubscriptionOrderResponse createOrder(
            @AuthenticationPrincipal UUID userId,
            @RequestBody CreateSubscriptionOrderRequest request
    ) {
        return subscriptionService.createOrder(userId, request);
    }

    @PostMapping("/verify-payment")
    public VerifySubscriptionPaymentResponse verifyPayment(
            @AuthenticationPrincipal UUID userId,
            @RequestBody VerifySubscriptionPaymentRequest request
    ) {
        return subscriptionService.verifyPayment(userId, request);
    }

    @PostMapping("/test-activate")
    public VerifySubscriptionPaymentResponse testActivate(
            @AuthenticationPrincipal UUID userId,
            @RequestBody VerifySubscriptionPaymentRequest request
    ) {
        return subscriptionService.testActivate(userId, request);
    }

}