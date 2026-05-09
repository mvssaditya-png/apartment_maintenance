package com.apartment.maintenance.service;

import com.apartment.maintenance.entity.Notification;
import com.apartment.maintenance.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepo;

    public void notifyUser(
            UUID userId,
            UUID siteId,
            String title,
            String message,
            String type) {

        Notification n = new Notification();

        n.setUserId(userId);
        n.setSiteId(siteId);
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);

        notificationRepo.save(n);
    }
}
