package com.apartment.maintenance.service;

import com.apartment.maintenance.dto.PayMaintenanceRequest;
import com.apartment.maintenance.dto.PaymentApprovalResponse;
import com.apartment.maintenance.dto.VerifyPaymentRequest;
import com.apartment.maintenance.entity.Flat;
import com.apartment.maintenance.entity.MaintenancePayment;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.exception.UnauthorizedActionException;
import com.apartment.maintenance.repository.FlatRepository;
import com.apartment.maintenance.repository.MaintenancePaymentRepository;
import com.apartment.maintenance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final MaintenancePaymentRepository paymentRepo;
    private final UserRepository userRepo;
    private final FlatRepository flatRepo;
    private final LedgerService ledgerService;
    private final NotificationService notificationService;

    public List<MaintenancePayment> getMyPendingPayments(UUID userId) {

        User user = userRepo.findById(userId).orElseThrow();

        Flat flat = flatRepo
                .findBySiteIdAndFlatNumber(
                        user.getSiteId(),
                        user.getFlatNumber())
                .orElseThrow();

        return paymentRepo.findByFlatIdAndPaymentStatus(
                flat.getFlatId(),
                "PENDING"
        );
    }
    @Transactional
    public void payMaintenance(
            UUID userId,
            PayMaintenanceRequest dto) {

        MaintenancePayment payment =
                paymentRepo.findById(dto.getPaymentId())
                        .orElseThrow();

        payment.setPaymentMode(dto.getPaymentMode());
        payment.setReceiptUrl(dto.getReceiptUrl());
        payment.setPaymentStatus("SUBMITTED");
        payment.setPaymentDate(LocalDateTime.now());

        paymentRepo.save(payment);
    }

    public void submitPayment(
            UUID userId,
            PayMaintenanceRequest dto) {

        MaintenancePayment payment =
                paymentRepo.findByPaymentId(dto.getPaymentId())
                        .orElseThrow();

        User user = userRepo.findById(userId).orElseThrow();

        // Ensure resident submits only own flat payment
        if (!payment.getFlatId().equals(user.getFlatId())) {
            throw new RuntimeException("Unauthorized payment");
        }

        payment.setPaymentMode(dto.getPaymentMode());
        payment.setReceiptUrl(dto.getReceiptUrl());
        payment.setPaymentStatus("SUBMITTED");
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepo.save(payment);
    }

    public List<PaymentApprovalResponse> pendingApprovals(UUID userId) {

        User user = userRepo.findById(userId)
                .orElseThrow();

        List<Object[]> rows =
                paymentRepo.getPendingApprovals(user.getSiteId());

        return rows.stream()
                .map(row -> new PaymentApprovalResponse(
                        (UUID) row[0],
                        (UUID) row[1],
                        (String) row[2],
                        (BigDecimal) row[3],
                        (Integer) row[4],
                        (Integer) row[5],
                        (String) row[6],
                        (String) row[7],
                        (String) row[8],
                        (String) row[9],
                        (LocalDateTime) row[10]
                ))
                .toList();
    }
    public void verifyPayment(
            UUID userId,
            VerifyPaymentRequest dto) {

        User user = userRepo.findById(userId).orElseThrow();

        if (!user.getRole().equalsIgnoreCase("ADMIN")
                && !user.getRole().equalsIgnoreCase("CASHIER")) {
            throw new UnauthorizedActionException("Not allowed");
        }

        MaintenancePayment payment =
                paymentRepo.findByPaymentId(dto.getPaymentId())
                        .orElseThrow();

        if (dto.isApproved()) {
            payment.setPaymentStatus("PAID");
            payment.setApprovedAt(LocalDateTime.now());
            ledgerService.addCredit(
                    user.getSiteId(),
                    payment.getAmount(),
                    "PAYMENT",
                    payment.getPaymentId(),
                    "Maintenance payment received"
            );

            notificationService.notifyUser(
                    userId,
                    payment.getSiteId(),
                    "Payment Approved",
                    "Your maintenance payment has been approved.",
                    "PAYMENT_APPROVED"
            );
        } else {
            payment.setPaymentStatus("REJECTED");
        }

        paymentRepo.save(payment);
    }
    @Transactional
    public void approvePayment(UUID paymentId) {

        MaintenancePayment payment =
                paymentRepo.findById(paymentId)
                        .orElseThrow();

        UUID userId = paymentRepo.getUserUUID(payment.getFlatId());
        ledgerService.addCredit(
                payment.getSiteId(),
                payment.getAmount(),
                "PAYMENT",
                payment.getPaymentId(),
                "Maintenance payment received"
        );
        payment.setPaymentStatus("PAID");

        paymentRepo.save(payment);
        notificationService.notifyUser(
                userId,
                payment.getSiteId(),
                "Payment Approved",
                "Your maintenance payment has been approved.",
                "PAYMENT_APPROVED"
        );

    }

    @Transactional
    public void rejectPayment(UUID paymentId) {

        MaintenancePayment payment =
                paymentRepo.findById(paymentId)
                        .orElseThrow();

        payment.setPaymentStatus("REJECTED");

        paymentRepo.save(payment);
    }

    public List<MaintenancePayment> getMyHistory(UUID userId) {

        User user = userRepo.findById(userId)
                .orElseThrow();

        Flat flat = flatRepo
                .findBySiteIdAndFlatNumber(
                        user.getSiteId(),
                        user.getFlatNumber())
                .orElseThrow();

        return paymentRepo
                .findByFlatIdAndPaymentStatusOrderByPaymentYearDescPaymentMonthDesc(
                        flat.getFlatId(),
                        "PAID"
                );
    }
}
