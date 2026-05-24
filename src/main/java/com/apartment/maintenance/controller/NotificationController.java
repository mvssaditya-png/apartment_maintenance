package com.apartment.maintenance.controller;

import com.apartment.maintenance.entity.Notification;
import com.apartment.maintenance.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepo;

    @GetMapping("/my")
    public List<Notification> myNotifications() {

        UUID userId =
                (UUID) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();

        return notificationRepo
                .findByUserIdOrderByIsReadAscCreatedAtDesc(userId);
    }

    @PutMapping("/{id}/read")
    public void markRead(@PathVariable UUID id) {

        Notification n =
                notificationRepo.findById(id).orElseThrow();

        n.setIsRead(true);

        notificationRepo.save(n);
    }
    @GetMapping("/unread-count")
    public long unreadCount() {

        UUID userId =
                (UUID) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();

        return notificationRepo.countByUserIdAndIsReadFalse(userId);
    }
}
