package com.apartment.maintenance.controller;

import com.apartment.maintenance.dto.CreateMeetingRequest;
import com.apartment.maintenance.dto.UpdateMeetingRequest;
import com.apartment.maintenance.entity.Meeting;
import com.apartment.maintenance.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @GetMapping
    public List<Meeting> getMeetings(
            @AuthenticationPrincipal UUID userId
    ) {
        return meetingService.getMeetings(userId);
    }

    @PostMapping
    public Meeting createMeeting(
            @AuthenticationPrincipal UUID userId,
            @RequestBody CreateMeetingRequest request
    ) {
        return meetingService.createMeeting(userId, request);
    }

    @PutMapping("/{meetingId}")
    public Meeting updateMeeting(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID meetingId,
            @RequestBody UpdateMeetingRequest request
    ) {
        return meetingService.updateMeeting(userId, meetingId, request);
    }

    @PatchMapping("/{meetingId}/toggle")
    public Meeting toggleMeeting(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID meetingId
    ) {
        return meetingService.toggleMeeting(userId, meetingId);
    }
}