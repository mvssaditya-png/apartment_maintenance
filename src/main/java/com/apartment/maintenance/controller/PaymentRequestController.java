package com.apartment.maintenance.controller;

import com.apartment.maintenance.dto.CreatePaymentRequest;
import com.apartment.maintenance.entity.PaymentRequest;
import com.apartment.maintenance.security.SecurityUtil;
import com.apartment.maintenance.service.PaymentRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@RestController
@RequestMapping("/api/payment-requests")
@RequiredArgsConstructor
public class PaymentRequestController {

    private final PaymentRequestService service;

    @PostMapping
    public PaymentRequest create(
            @RequestBody CreatePaymentRequest dto) {
        UUID userId = SecurityUtil.getCurrentUserId();

        return service.createRequest(userId, dto);
    }
}