package com.apartment.maintenance.controller;

import com.apartment.maintenance.dto.CreateComplaintRequest;
import com.apartment.maintenance.dto.UpdateComplaintStatusRequest;
import com.apartment.maintenance.entity.Complaint;
import com.apartment.maintenance.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @GetMapping
    public List<Complaint> getComplaints(
            @AuthenticationPrincipal UUID userId
    ) {
        return complaintService.getComplaints(userId);
    }

    @PostMapping
    public Complaint createComplaint(
            @AuthenticationPrincipal UUID userId,
            @RequestBody CreateComplaintRequest request
    ) {
        return complaintService.createComplaint(userId, request);
    }

    @PatchMapping("/{complaintId}/status")
    public Complaint updateStatus(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID complaintId,
            @RequestBody UpdateComplaintStatusRequest request
    ) {
        return complaintService.updateComplaintStatus(
                userId,
                complaintId,
                request
        );
    }
}