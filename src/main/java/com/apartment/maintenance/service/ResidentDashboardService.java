package com.apartment.maintenance.service;

import com.apartment.maintenance.dto.ResidentDashboardDTO;
import com.apartment.maintenance.entity.Flat;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResidentDashboardService {

    private final MaintenancePaymentRepository paymentRepo;
    private final SocietyLedgerRepository ledgerRepo;
    private final UserService userService; // already exists
    private final FlatRepository flatRepository;

    public ResidentDashboardDTO getResidentDashboard() {

        /* ✅ Logged-in user */
        User user = userService.getLoggedInUser();

        /* ✅ User flat */
        Flat flat =
                flatRepository.findById(user.getFlatId())
                        .orElseThrow(() ->
                                new RuntimeException("Flat not found"));

        BigDecimal pending =
                paymentRepo.getPendingAmount(flat.getFlatId());

        BigDecimal totalPaid =
                paymentRepo.getTotalPaid(flat.getFlatId());

        Long pendingMonths =
                paymentRepo.getPendingMonths(flat.getFlatId());

        LocalDateTime lastPayment =
                paymentRepo.getLastPaymentDate(flat.getFlatId());

        BigDecimal societyBalance =
                ledgerRepo.getCurrentBalance(flat.getSiteId());

        return new ResidentDashboardDTO(
                flat.getFlatNumber(),
                flat.getOwnerName(),
                pending,
                totalPaid,
                pendingMonths,
                lastPayment,
                societyBalance
        );
    }
}
