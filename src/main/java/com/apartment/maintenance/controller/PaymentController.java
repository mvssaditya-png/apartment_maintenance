package com.apartment.maintenance.controller;

import com.apartment.maintenance.dto.*;
import com.apartment.maintenance.entity.MaintenancePayment;
import com.apartment.maintenance.entity.SocietyBalance;
import com.apartment.maintenance.repository.SocietyBalanceRepository;
import com.apartment.maintenance.repository.UserRepository;
import com.apartment.maintenance.security.SecurityUtil;
import com.apartment.maintenance.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;
    private final UserRepository userRepo;
    private final SocietyBalanceRepository balanceRepo;

    @GetMapping("/my-dues")
    public List<MyDueResponse> myDues() {
        UUID userId = SecurityUtil.getCurrentUserId();
        return service.getMyPendingPayments(userId);
    }

    @PostMapping("/pay")
    public String pay(
            @AuthenticationPrincipal UUID userId,
            @RequestBody PayMaintenanceRequest dto) {

        service.payMaintenance(userId, dto);

        return "Payment submitted for verification";
    }

    @GetMapping("/approvals")
    public List<PaymentApprovalResponse> approvals(
            @AuthenticationPrincipal UUID userId
    ) {
        return service.pendingApprovals(userId);
    }

    @PostMapping("/approve/{id}")
    public String approve(@PathVariable UUID id) {
        service.approvePayment(id);
        return "Payment approved";
    }

    @PostMapping("/reject/{id}")
    public String reject(@PathVariable UUID id) {
        service.rejectPayment(id);
        return "Payment rejected";
    }

    @GetMapping("/balance")
    public SocietyBalance balance(
            @AuthenticationPrincipal UUID userId) {

        UUID siteId = userRepo.findById(userId)
                .orElseThrow()
                .getSiteId();

        return balanceRepo.findById(siteId).orElseThrow();
    }

    // Resident submits payment
    @PostMapping("/submit")
    public String submit(
            @RequestBody PayMaintenanceRequest dto) {

        service.submitPayment(
                SecurityUtil.getCurrentUserId(),
                dto
        );

        return "Payment submitted successfully";
    }

    // Cashier/Admin approves payment
    @PostMapping("/verify")
    public String verify(
            @RequestBody VerifyPaymentRequest dto) {

        service.verifyPayment(
                SecurityUtil.getCurrentUserId(),
                dto
        );

        return "Payment verification completed";
    }

    @GetMapping("/my-history")
    public List<MaintenancePayment> history(
            @AuthenticationPrincipal UUID userId) {

        return service.getMyHistory(userId);
    }

    @GetMapping("/flat/{flatId}/pending")
    public List<MaintenancePayment> getFlatPendingPayments(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID flatId
    ) {
        return service.getPendingPaymentsByFlat(userId, flatId);
    }

    @PostMapping("/record")
    public String recordPayment(
            @AuthenticationPrincipal UUID userId,
            @RequestBody RecordPaymentRequest request
    ) {
        return service.recordPayment(userId, request);
    }
}
