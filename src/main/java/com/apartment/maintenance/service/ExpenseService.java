package com.apartment.maintenance.service;

import com.apartment.maintenance.dto.CreateExpenseRequest;
import com.apartment.maintenance.entity.Expense;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.exception.UnauthorizedActionException;
import com.apartment.maintenance.repository.ExpenseRepository;
import com.apartment.maintenance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepo;
    private final UserRepository userRepo;
    private final LedgerService ledgerService;
    public Expense createExpense(
            UUID userId,
            CreateExpenseRequest dto) {

        User user = userRepo.findById(userId)
                .orElseThrow();

        // ROLE CHECK
        if (!user.getRole().equalsIgnoreCase("ADMIN")
                && !user.getRole().equalsIgnoreCase("CASHIER")) {

            throw new UnauthorizedActionException(
                    "Only Admin or Cashier can add expense"
            );
        }

        Expense expense = new Expense();

        expense.setSiteId(user.getSiteId());
        expense.setTitle(dto.getTitle());
        expense.setDescription(dto.getDescription());
        expense.setAmount(dto.getAmount());
        expense.setExpenseDate(dto.getExpenseDate());
        expense.setCategory(dto.getCategory());
        expense.setReceiptUrl(dto.getReceiptUrl());
        expense.setCreatedBy(userId);
        ledgerService.addDebit(
                user.getSiteId(),
                expense.getAmount(),
                "EXPENSE",
                expense.getExpenseId(),
                expense.getTitle()
        );
        return expenseRepo.save(expense);
    }

    public List<Expense> getSiteExpenses(UUID userId) {

        UUID siteId = userRepo.findById(userId)
                .orElseThrow()
                .getSiteId();

        return expenseRepo.findBySiteIdOrderByExpenseDateDesc(siteId);
    }
}
