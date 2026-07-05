package com.apartment.maintenance.service;

import com.apartment.maintenance.dto.*;
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
    private final ReceiptPdfService receiptPdfService;
    private final SmsEventService smsEventService;
    public List<MyDueResponse> getMyPendingPayments(UUID userId) {

        User user = userRepo.findById(userId).orElseThrow();

        return paymentRepo.findMyDuesWithRequestDetails(
                user.getFlatId()
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
        smsEventService.paymentSubmitted(user, payment);
    }

    public List<PaymentApprovalResponse> pendingApprovals(UUID userId) {

        User user = userRepo.findById(userId)
                .orElseThrow();

        List<Object[]> rows;

        if (user.getRole().equalsIgnoreCase("RESIDENT")) {
            rows = paymentRepo.getPendingApprovalsByFlatId(user.getFlatId());
        } else {
            rows = paymentRepo.getPendingApprovals(user.getSiteId());
        }

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
            String receiptPdfUrl = receiptPdfService.generateReceiptPdf(payment);
            payment.setReceiptPdfUrl(receiptPdfUrl);
            paymentRepo.save(payment);

            notificationService.notifyUser(
                    userId,
                    payment.getSiteId(),
                    "Payment Approved",
                    "Your payment of ₹" + payment.getAmount()
                            + " for "
                            + payment.getPaymentMonth()
                            + "/" + payment.getPaymentYear()
                            + " has been approved successfully.",
                    "PAYMENT_APPROVED"
            );
            smsEventService.paymentSubmitted(user, payment);
        } else {
            payment.setPaymentStatus("REJECTED");
            notificationService.notifyUser(
                    userId,
                    payment.getSiteId(),
                    "Payment Rejected",
                    "Your payment of ₹" + payment.getAmount()
                            + " for "
                            + payment.getPaymentMonth()
                            + "/" + payment.getPaymentYear()
                            + " was rejected. Please re-submit payment receipt.",
                    "PAYMENT_REJECTED"
            );
            smsEventService.paymentSubmitted(user, payment);
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
        String receiptPdfUrl = receiptPdfService.generateReceiptPdf(payment);
        payment.setReceiptPdfUrl(receiptPdfUrl);
        paymentRepo.save(payment);
        paymentRepo.save(payment);
        notificationService.notifyUser(
                userId,
                payment.getSiteId(),
                "Payment Approved",
                "Your payment of ₹" + payment.getAmount()
                        + " for "
                        + payment.getPaymentMonth()
                        + "/" + payment.getPaymentYear()
                        + " has been approved successfully.",
                "PAYMENT_APPROVED"
        );
        User resident = userRepo.findById(userId).orElse(null);
        smsEventService.paymentApproved(resident, payment);

    }

    @Transactional
    public void rejectPayment(UUID paymentId) {

        MaintenancePayment payment =
                paymentRepo.findById(paymentId)
                        .orElseThrow();
        UUID userId = paymentRepo.getUserUUID(payment.getFlatId());
        payment.setPaymentStatus("REJECTED");
        notificationService.notifyUser(
                userId,
                payment.getSiteId(),
                "Payment Rejected",
                "Your payment of ₹" + payment.getAmount()
                        + " for "
                        + payment.getPaymentMonth()
                        + "/" + payment.getPaymentYear()
                        + " was rejected. Please re-submit payment receipt.",
                "PAYMENT_REJECTED"
        );
        User resident = userRepo.findById(userId).orElse(null);
        smsEventService.paymentRejected(resident, payment, "Please re-submit payment receipt");
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

    public List<MaintenancePayment> getPendingPaymentsByFlat(UUID userId, UUID flatId) {

        User user = userRepo.findById(userId).orElseThrow();

        if (!user.getRole().equalsIgnoreCase("ADMIN")
                && !user.getRole().equalsIgnoreCase("CASHIER")) {
            throw new UnauthorizedActionException(
                    "Only Admin or Cashier can view flat pending payments"
            );
        }

        return paymentRepo.findByFlatIdAndPaymentStatusNot(
                flatId,
                "PAID"
        );
    }

    @Transactional
    public String recordPayment(UUID userId, RecordPaymentRequest request) {

        User user = userRepo.findById(userId).orElseThrow();

        if (!user.getRole().equalsIgnoreCase("ADMIN")
                && !user.getRole().equalsIgnoreCase("CASHIER")) {
            throw new UnauthorizedActionException(
                    "Only Admin or Cashier can record payment"
            );
        }

        MaintenancePayment payment =
                paymentRepo.findByPaymentId(request.getPaymentId())
                        .orElseThrow();

        payment.setPaymentMode(request.getPaymentMode());
        payment.setReceiptUrl(request.getReceiptUrl());
        payment.setPaymentStatus("PAID");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setApprovedAt(LocalDateTime.now());

        paymentRepo.save(payment);

        // Reuse your existing ledger/balance update logic here.
        // Same logic used when cashier approves submitted payment.

        ledgerService.addCredit(
                payment.getSiteId(),
                payment.getAmount(),
                "PAYMENT",
                payment.getPaymentId(),
                "Maintenance payment received"
        );
        String receiptPdfUrl = receiptPdfService.generateReceiptPdf(payment);
        payment.setReceiptPdfUrl(receiptPdfUrl);
        paymentRepo.save(payment);
        UUID residentUserId = paymentRepo.getUserUUID(payment.getFlatId());

        notificationService.notifyUser(
                residentUserId,
                payment.getSiteId(),
                "Payment Recorded",
                "A payment of ₹" + payment.getAmount()
                        + " for "
                        + payment.getPaymentMonth()
                        + "/" + payment.getPaymentYear()
                        + " has been recorded by Admin.",
                "PAYMENT_RECORDED"
        );

        User resident = userRepo.findById(residentUserId).orElse(null);
        smsEventService.directPaymentRecorded(resident, payment);

        return "Payment recorded successfully";
    }
}
