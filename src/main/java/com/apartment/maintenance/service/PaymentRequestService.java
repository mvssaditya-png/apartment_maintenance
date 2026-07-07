package com.apartment.maintenance.service;

import com.apartment.maintenance.dto.CreatePaymentRequest;
import com.apartment.maintenance.entity.Flat;
import com.apartment.maintenance.entity.MaintenancePayment;
import com.apartment.maintenance.entity.PaymentRequest;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.exception.DuplicateMaintenanceException;
import com.apartment.maintenance.repository.FlatRepository;
import com.apartment.maintenance.repository.MaintenancePaymentRepository;
import com.apartment.maintenance.repository.PaymentRequestRepository;
import com.apartment.maintenance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final SmsEventService smsEventService;
    @Transactional
    public PaymentRequest createRequest(UUID userId, CreatePaymentRequest dto) {

        UUID siteId = userRepo.findById(userId)
                .orElseThrow()
                .getSiteId();

        String requestType = dto.getRequestType();

        boolean isMaintenance =
                "Maintenance".equalsIgnoreCase(requestType);

        /*
         * RULE:
         * Maintenance       -> only one per site/month/year
         * Special Request   -> unlimited allowed
         */
        if (isMaintenance) {

            boolean alreadyGenerated =
                    requestRepo.existsBySiteIdAndPaymentMonthAndPaymentYearAndRequestType(
                            siteId,
                            dto.getPaymentMonth(),
                            dto.getPaymentYear(),
                            requestType
                    );

            if (alreadyGenerated) {
                throw new DuplicateMaintenanceException(
                        "Maintenance already generated for this month and year"
                );
            }
        }

        PaymentRequest request = new PaymentRequest();

        request.setSiteId(siteId);
        request.setTitle(dto.getTitle());
        request.setDescription(dto.getDescription());
        request.setAmount(dto.getAmount());
        request.setPaymentMonth(dto.getPaymentMonth());
        request.setPaymentYear(dto.getPaymentYear());
        request.setRequestType(requestType);
        request.setStatus("generated");
        request.setCreatedBy(userId);
        request.setDueDate(dto.getDueDate());

        requestRepo.save(request);

        List<Flat> flats =
                flatRepo.findBySiteIdAndIsActiveTrue(siteId);

        List<MaintenancePayment> payments = new ArrayList<>();

        for (Flat flat : flats) {

            /*
             * Extra safety:
             * Only skip duplicate flat payments for Maintenance.
             * Special Requests must always generate new payment rows.
             */
            if (isMaintenance) {
                boolean paymentAlreadyExists =
                        paymentRepo.existsByFlatIdAndPaymentMonthAndPaymentYearAndRequestType(
                                flat.getFlatId(),
                                dto.getPaymentMonth(),
                                dto.getPaymentYear(),
                                requestType
                        );

                if (paymentAlreadyExists) {
                    continue;
                }
            }

            MaintenancePayment payment = new MaintenancePayment();

            payment.setSiteId(siteId);
            payment.setFlatId(flat.getFlatId());
            payment.setAmount(BigDecimal.valueOf(dto.getAmount()));
            payment.setPaymentMonth(dto.getPaymentMonth());
            payment.setPaymentYear(dto.getPaymentYear());
            payment.setPaymentStatus("PENDING");
            payment.setRequestId(request.getRequestId());
            payment.setRequestType(requestType);

            payments.add(payment);
        }

        paymentRepo.saveAll(payments);

        List<User> users =
                userRepo.findBySiteIdAndIsActive(siteId, true);

        for (User user : users) {
            notificationService.notifyUser(
                    user.getUserId(),
                    siteId,
                    dto.getTitle(),
                    dto.getTitle()
                            + " for "
                            + dto.getPaymentMonth()
                            + "/"
                            + dto.getPaymentYear()
                            + " has been generated.",
                    requestType
            );
            smsEventService.paymentRequestCreated(user, request);
        }

        return request;
    }
}