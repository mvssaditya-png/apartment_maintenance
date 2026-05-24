package com.apartment.maintenance.controller;

import com.apartment.maintenance.dto.CreateScheduledPaymentRequest;
import com.apartment.maintenance.dto.UpdateScheduledPaymentRequest;
import com.apartment.maintenance.entity.ScheduledPaymentRequest;
import com.apartment.maintenance.service.ScheduledPaymentRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/scheduled-payment-requests")
@RequiredArgsConstructor
public class ScheduledPaymentRequestController {

    private final ScheduledPaymentRequestService service;

    @GetMapping
    public List<ScheduledPaymentRequest> getSchedules(
            @AuthenticationPrincipal UUID userId
    ) {
        return service.getSchedules(userId);
    }

    @PostMapping
    public ScheduledPaymentRequest createSchedule(
            @AuthenticationPrincipal UUID userId,
            @RequestBody CreateScheduledPaymentRequest request
    ) {
        return service.createSchedule(userId, request);
    }

    @PutMapping("/{scheduleId}")
    public ScheduledPaymentRequest updateSchedule(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID scheduleId,
            @RequestBody UpdateScheduledPaymentRequest request
    ) {
        return service.updateSchedule(userId, scheduleId, request);
    }

    @PatchMapping("/{scheduleId}/toggle")
    public ScheduledPaymentRequest toggleSchedule(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID scheduleId
    ) {
        return service.toggleSchedule(userId, scheduleId);
    }
}