package com.apartment.maintenance.service;

import com.apartment.maintenance.dto.DefaulterResponse;
import com.apartment.maintenance.dto.MonthlySummaryResponse;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.exception.UnauthorizedActionException;
import com.apartment.maintenance.repository.ExpenseRepository;
import com.apartment.maintenance.repository.MaintenancePaymentRepository;
import com.apartment.maintenance.repository.SocietyBalanceRepository;
import com.apartment.maintenance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final MaintenancePaymentRepository paymentRepo;
    private final UserRepository userRepo;
    private final ExpenseRepository expenseRepo;
    private final SocietyBalanceRepository balanceRepo;

    public List<DefaulterResponse> getDefaulters(UUID userId) {

        User user = userRepo.findById(userId)
                .orElseThrow();

        if (!user.getRole().equalsIgnoreCase("ADMIN")
                && !user.getRole().equalsIgnoreCase("CASHIER")) {

            throw new UnauthorizedActionException(
                    "Only Admin or Cashier can view defaulters"
            );
        }

        return paymentRepo.findDefaulters(user.getSiteId());
    }

    public MonthlySummaryResponse getMonthlySummary(
            UUID userId,
            String month,
            String year
    ) {

        User user = userRepo.findById(userId)
                .orElseThrow();

        UUID siteId = user.getSiteId();

        double collections =
                paymentRepo.getMonthlyCollections(siteId, month, year);

        double expenses =
                expenseRepo.getMonthlyExpenses(siteId, month, year);

        double previousCollections =
                paymentRepo.getCollectionsBefore(siteId, month, year);

        double previousExpenses =
                expenseRepo.getExpensesBefore(siteId, month, year);

        double siteOpeningBalance =
                balanceRepo.getOpeningBalance(siteId);

        double openingBalance =
                siteOpeningBalance + previousCollections - previousExpenses;

        double closingBalance =
                openingBalance + collections - expenses;

        return new MonthlySummaryResponse(
                openingBalance,
                collections,
                expenses,
                closingBalance
        );
    }
}