package com.apartment.maintenance.service;

import com.apartment.maintenance.dto.DashboardResponse;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
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

        // Society Balance
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

        // Current month payment for logged user
        LocalDate now = LocalDate.now();

        var myPayment =
                paymentRepo.findByFlatIdAndPaymentMonthAndPaymentYear(
                        user.getFlatId(),
                        now.getMonthValue(),
                        now.getYear()
                );

        BigDecimal myPendingAmount = BigDecimal.ZERO;
        String myStatus = "NO_DUE";

        if (myPayment.isPresent()) {
            myPendingAmount = myPayment.get().getAmount();
            myStatus = myPayment.get().getPaymentStatus();
        }

        return DashboardResponse.builder()
                .currentBalance(balance.getCurrentBalance())
                .totalCollected(balance.getTotalCollected())
                .totalExpenses(balance.getTotalExpenses())
                .totalFlats(totalFlats)
                .paidFlats(paidFlats)
                .pendingFlats(pendingFlats)
                .myPendingAmount(myPendingAmount)
                .myPaymentStatus(myStatus)
                .build();
    }
}