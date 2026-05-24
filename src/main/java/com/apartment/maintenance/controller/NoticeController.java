package com.apartment.maintenance.controller;

import com.apartment.maintenance.dto.CreateNoticeRequest;
import com.apartment.maintenance.entity.Notice;
import com.apartment.maintenance.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public List<Notice> getNotices(
            @AuthenticationPrincipal UUID userId
    ) {
        return noticeService.getNotices(userId);
    }

    @PostMapping
    public Notice createNotice(
            @AuthenticationPrincipal UUID userId,
            @RequestBody CreateNoticeRequest request
    ) {
        return noticeService.createNotice(userId, request);
    }

    @PatchMapping("/{noticeId}/toggle")
    public Notice toggleNotice(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID noticeId
    ) {
        return noticeService.toggleNotice(userId, noticeId);
    }
}