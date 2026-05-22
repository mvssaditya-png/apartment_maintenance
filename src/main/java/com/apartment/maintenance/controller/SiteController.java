package com.apartment.maintenance.controller;

import com.apartment.maintenance.dto.SiteProfileDTO;
import com.apartment.maintenance.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sites")
@RequiredArgsConstructor
public class SiteController {

    private final SiteService siteService;

    @GetMapping("/me")
    public SiteProfileDTO getMySite(
            @AuthenticationPrincipal UUID userId
    ) {
        return siteService.getMySite(userId);
    }
}
