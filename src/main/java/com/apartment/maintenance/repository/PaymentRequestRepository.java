package com.apartment.maintenance.repository;

import com.apartment.maintenance.entity.PaymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PaymentRequestRepository
        extends JpaRepository<PaymentRequest, UUID> {

    List<PaymentRequest> findBySiteId(UUID siteId);
    boolean existsBySiteIdAndPaymentMonthAndPaymentYear(
            UUID siteId,
            Integer paymentMonth,
            Integer paymentYear
    );

}