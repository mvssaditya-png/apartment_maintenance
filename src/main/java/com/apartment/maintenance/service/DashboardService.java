package com.apartment.maintenance.service;

import com.apartment.maintenance.dto.DashboardResponse;
import com.apartment.maintenance.entity.MaintenancePayment;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.repository.FlatRepository;
import com.apartment.maintenance.repository.MaintenancePaymentRepository;
import com.apartment.maintenance.repository.SocietyBalanceProjection;
import com.apartment.maintenance.repository.SocietyBalanceRepository;
import com.apartment.maintenance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepo;
    private final SocietyBalanceRepository balanceRepo;
    private final MaintenancePaymentRepository paymentRepo;
    private final FlatRepository flatRepo;

    public DashboardResponse getDashboard(UUID userId) {

        User user = userRepo.findById(userId).orElseThrow();

        UUID siteId = user.getSiteId();

        SocietyBalanceProjection balance =
                balanceRepo.findBalance(siteId);

        long totalFlats =
                flatRepo.countBySiteIdAndIsActiveTrue(siteId);

        long paidFlats =
                paymentRepo.countBySiteIdAndPaymentStatus(
                        siteId,
                        "PAID"
                );

        long pendingFlats = totalFlats - paidFlats;

        LocalDate now = LocalDate.now();

        List<MaintenancePayment> myPayments =
                paymentRepo.findByFlatIdAndPaymentMonthAndPaymentYear(
                        user.getFlatId(),
                        now.getMonthValue(),
                        now.getYear()
                );

        BigDecimal myPendingAmount = BigDecimal.ZERO;
        String myStatus = "NO_DUE";

        if (!myPayments.isEmpty()) {

            myPendingAmount = myPayments.stream()
                    .filter(payment ->
                            !"PAID".equalsIgnoreCase(payment.getPaymentStatus())
                    )
                    .map(MaintenancePayment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            boolean hasPending = myPayments.stream()
                    .anyMatch(payment ->
                            "PENDING".equalsIgnoreCase(payment.getPaymentStatus())
                    );

            boolean hasSubmitted = myPayments.stream()
                    .anyMatch(payment ->
                            "SUBMITTED".equalsIgnoreCase(payment.getPaymentStatus())
                    );

            boolean allPaid = myPayments.stream()
                    .allMatch(payment ->
                            "PAID".equalsIgnoreCase(payment.getPaymentStatus())
                    );

            if (hasPending) {
                myStatus = "PENDING";
            } else if (hasSubmitted) {
                myStatus = "SUBMITTED";
            } else if (allPaid) {
                myStatus = "PAID";
            }
        }

        return DashboardResponse.builder()
                .currentBalance(
                        balance != null && balance.getCurrentBalance() != null
                                ? balance.getCurrentBalance()
                                : BigDecimal.ZERO
                )
                .totalCollected(
                        balance != null && balance.getTotalCollected() != null
                                ? balance.getTotalCollected()
                                : BigDecimal.ZERO
                )
                .totalExpenses(
                        balance != null && balance.getTotalExpenses() != null
                                ? balance.getTotalExpenses()
                                : BigDecimal.ZERO
                )
                .totalFlats(totalFlats)
                .paidFlats(paidFlats)
                .pendingFlats(pendingFlats)
                .myPendingAmount(myPendingAmount)
                .myPaymentStatus(myStatus)
                .build();
    }
}