package com.apartment.maintenance.controller;

import com.apartment.maintenance.dto.AdminUserRequest;
import com.apartment.maintenance.dto.AdminUserResponse;
import com.apartment.maintenance.service.AdminUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/site/{siteId}")
    public List<AdminUserResponse> getUsersBySite(@PathVariable UUID siteId) {
        return adminUserService.getUsersBySite(siteId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public AdminUserResponse createUser(@RequestBody AdminUserRequest request) {
        return adminUserService.createUser(request);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}")
    public AdminUserResponse updateUser(
            @PathVariable UUID userId,
            @RequestBody AdminUserRequest request
    ) {
        return adminUserService.updateUser(userId, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/site/{siteId}/owners")
    public List<AdminUserResponse> getOwnersBySite(@PathVariable UUID siteId) {
        return adminUserService.getOwnersBySite(siteId);
    }
}