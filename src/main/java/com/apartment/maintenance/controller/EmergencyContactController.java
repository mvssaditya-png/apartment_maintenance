package com.apartment.maintenance.controller;

import com.apartment.maintenance.dto.CreateEmergencyContactRequest;
import com.apartment.maintenance.dto.UpdateEmergencyContactRequest;
import com.apartment.maintenance.entity.EmergencyContact;
import com.apartment.maintenance.service.EmergencyContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/emergency-contacts")
@RequiredArgsConstructor
public class EmergencyContactController {

    private final EmergencyContactService service;

    @GetMapping
    public List<EmergencyContact> getContacts(
            @AuthenticationPrincipal UUID userId
    ) {
        return service.getContacts(userId);
    }

    @PostMapping
    public EmergencyContact createContact(
            @AuthenticationPrincipal UUID userId,
            @RequestBody CreateEmergencyContactRequest request
    ) {
        return service.createContact(userId, request);
    }

    @PutMapping("/{contactId}")
    public EmergencyContact updateContact(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID contactId,
            @RequestBody UpdateEmergencyContactRequest request
    ) {
        return service.updateContact(userId, contactId, request);
    }

    @PatchMapping("/{contactId}/toggle")
    public EmergencyContact toggleContact(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID contactId
    ) {
        return service.toggleContact(userId, contactId);
    }
}