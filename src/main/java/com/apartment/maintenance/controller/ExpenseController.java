package com.apartment.maintenance.controller;

import com.apartment.maintenance.dto.CreateExpenseRequest;
import com.apartment.maintenance.entity.Expense;
import com.apartment.maintenance.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
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

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExpenses(
            @AuthenticationPrincipal UUID userId
    ) {
        byte[] excelBytes = service.exportExpensesExcel(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ));
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("expenses-report.xlsx")
                        .build()
        );

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}
