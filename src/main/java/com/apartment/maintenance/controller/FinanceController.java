package com.apartment.maintenance.controller;

import com.apartment.maintenance.service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService service;

    // ------------------------
    // GET OPENING BALANCE
    // ------------------------
    @GetMapping("/opening-balance")
    public BigDecimal getOpeningBalance(
            @AuthenticationPrincipal UUID userId) {

        return service.getOpeningBalance(userId);
    }

    // ------------------------
    // UPDATE OPENING BALANCE
    // ------------------------
    @PutMapping("/update-opening-balance")
    public BigDecimal updateOpeningBalance(
            @AuthenticationPrincipal UUID userId,
            @RequestBody Map<String, BigDecimal> body) {

        return service.updateOpeningBalance(
                userId,
                body.get("amount"));
    }
}
