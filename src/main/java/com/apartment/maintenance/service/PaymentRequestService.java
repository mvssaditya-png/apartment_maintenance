package com.apartment.maintenance.service;

import com.apartment.maintenance.dto.CreatePaymentRequest;
import com.apartment.maintenance.entity.*;
import com.apartment.maintenance.exception.DuplicateMaintenanceException;
import com.apartment.maintenance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentRequestService {

    private final PaymentRequestRepository requestRepo;
    private final FlatRepository flatRepo;
    private final MaintenancePaymentRepository paymentRepo;
    private final UserRepository userRepo;
    private final NotificationService notificationService;
    @Transactional
    public PaymentRequest createRequest(
            UUID userId,
            CreatePaymentRequest dto) {

        UUID siteId = userRepo.findById(userId)
                .orElseThrow()
                .getSiteId();

        // ✅ CHECK IF REQUEST ALREADY GENERATED
        boolean alreadyGenerated =
                requestRepo.existsBySiteIdAndPaymentMonthAndPaymentYear(
                        siteId,
                        dto.getPaymentMonth(),
                        dto.getPaymentYear()
                );

        if (alreadyGenerated) {
            throw new DuplicateMaintenanceException(
                    "Maintenance already generated for this month and year"
            );
        }

        // -----------------------
        // CREATE PAYMENT REQUEST
        // -----------------------

        PaymentRequest request = new PaymentRequest();

        request.setSiteId(siteId);
        request.setTitle(dto.getTitle());
        request.setAmount(dto.getAmount());
        request.setPaymentMonth(dto.getPaymentMonth());
        request.setPaymentYear(dto.getPaymentYear());
        request.setRequestType("MAINTENANCE");
        request.setStatus("generated");
        request.setCreatedBy(userId);
        request.setDueDate(dto.getDueDate());

        requestRepo.save(request);

        // -----------------------
        // GENERATE PAYMENTS
        // -----------------------

        List<Flat> flats =
                flatRepo.findBySiteIdAndIsActiveTrue(siteId);

        List<MaintenancePayment> payments = new ArrayList<>();

        for (Flat flat : flats) {

            // EXTRA SAFETY CHECK
            if (paymentRepo
                    .existsByFlatIdAndPaymentMonthAndPaymentYear(
                            flat.getFlatId(),
                            dto.getPaymentMonth(),
                            dto.getPaymentYear())) {
                continue;
            }

            MaintenancePayment payment = new MaintenancePayment();

            payment.setSiteId(siteId);
            payment.setFlatId(flat.getFlatId());
            payment.setAmount(BigDecimal.valueOf(dto.getAmount()));
            payment.setPaymentMonth(dto.getPaymentMonth());
            payment.setPaymentYear(dto.getPaymentYear());
            payment.setPaymentStatus("PENDING");
            payment.setRequestId(request.getRequestId());

            payments.add(payment);
        }

        paymentRepo.saveAll(payments);
        List<User> residents =
                userRepo.findBySiteIdAndRole(siteId, "RESIDENT");

        for (User user : residents) {

            notificationService.notifyUser(
                    user.getUserId(),
                    siteId,
                    "Maintenance Generated",
                    "Maintenance for "
                            + dto.getPaymentMonth()
                            + "/" + dto.getPaymentYear()
                            + " has been generated.",
                    "MAINTENANCE"
            );
        }
        return request;
    }
}