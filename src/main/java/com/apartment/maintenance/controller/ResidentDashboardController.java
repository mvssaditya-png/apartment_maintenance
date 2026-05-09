package com.apartment.maintenance.controller;

import com.apartment.maintenance.dto.ResidentDashboardDTO;
import com.apartment.maintenance.service.ResidentDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class ResidentDashboardController {

    private final ResidentDashboardService dashboardService;

    @GetMapping("/resident")
    public ResponseEntity<ResidentDashboardDTO> getDashboard() {

        return ResponseEntity.ok(
                dashboardService.getResidentDashboard()
        );
    }
}
