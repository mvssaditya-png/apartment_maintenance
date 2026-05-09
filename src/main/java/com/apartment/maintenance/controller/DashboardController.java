package com.apartment.maintenance.controller;

import com.apartment.maintenance.dto.DashboardResponse;
import com.apartment.maintenance.service.DashboardService;
import com.apartment.maintenance.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping
    public DashboardResponse dashboard() {

        return service.getDashboard(
                SecurityUtil.getCurrentUserId()
        );
    }
}