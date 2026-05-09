package com.apartment.maintenance.controller;

import com.apartment.maintenance.dto.DefaulterResponse;
import com.apartment.maintenance.dto.MonthlySummaryResponse;
import com.apartment.maintenance.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService service;

    @GetMapping("/defaulters")
    public List<DefaulterResponse> defaulters(
            @AuthenticationPrincipal UUID userId) {

        return service.getDefaulters(userId);
    }

    @GetMapping("/monthly-summary")
    public MonthlySummaryResponse monthlySummary(
            @AuthenticationPrincipal UUID userId,
            @RequestParam Integer month,
            @RequestParam Integer year) {

        return service.getMonthlySummary(userId, month, year);
    }
}
