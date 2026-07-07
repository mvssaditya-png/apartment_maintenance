package com.apartment.maintenance.controller;

import com.apartment.maintenance.dto.SuperAdminFlatRequest;
import com.apartment.maintenance.dto.SuperAdminFlatResponse;
import com.apartment.maintenance.service.SuperAdminFlatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/super-admin")
@RequiredArgsConstructor
public class SuperAdminFlatController {

    private final SuperAdminFlatService superAdminFlatService;

    @GetMapping("/sites/{siteId}/flats")
    public List<SuperAdminFlatResponse> getFlats(
            @PathVariable UUID siteId
    ) {
        return superAdminFlatService.getFlats(siteId);
    }

    @PostMapping("/sites/{siteId}/flats")
    public SuperAdminFlatResponse addFlat(
            @PathVariable UUID siteId,
            @RequestBody SuperAdminFlatRequest request
    ) {
        return superAdminFlatService.addFlat(siteId, request);
    }

    @PutMapping("/flats/{flatId}")
    public SuperAdminFlatResponse updateFlat(
            @PathVariable UUID flatId,
            @RequestBody SuperAdminFlatRequest request
    ) {
        return superAdminFlatService.updateFlat(flatId, request);
    }

    @PatchMapping("/flats/{flatId}/toggle")
    public SuperAdminFlatResponse toggleFlat(
            @PathVariable UUID flatId
    ) {
        return superAdminFlatService.toggleFlat(flatId);
    }
}