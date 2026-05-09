package com.apartment.maintenance.controller;

import com.apartment.maintenance.dto.CreateExpenseRequest;
import com.apartment.maintenance.entity.Expense;
import com.apartment.maintenance.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService service;

    @PostMapping("/add")
    public Expense addExpense(
            @AuthenticationPrincipal UUID userId,
            @RequestBody CreateExpenseRequest dto) {

        return service.createExpense(userId, dto);
    }

    @GetMapping("/view")
    public List<Expense> getExpenses(
            @AuthenticationPrincipal UUID userId) {

        return service.getSiteExpenses(userId);
    }
}
