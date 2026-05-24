package com.apartment.maintenance.service;

import com.apartment.maintenance.dto.CreateNoticeRequest;
import com.apartment.maintenance.entity.Notice;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.exception.UnauthorizedActionException;
import com.apartment.maintenance.repository.NoticeRepository;
import com.apartment.maintenance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepo;
    private final UserRepository userRepo;
    private final NotificationService notificationService;

    public List<Notice> getNotices(UUID userId) {
        User user = userRepo.findById(userId).orElseThrow();

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            return noticeRepo.findBySiteIdOrderByCreatedAtDesc(user.getSiteId());
        }

        return noticeRepo.findBySiteIdAndActiveTrueOrderByCreatedAtDesc(
                user.getSiteId()
        );
    }

    public Notice createNotice(UUID userId, CreateNoticeRequest request) {
        User user = userRepo.findById(userId).orElseThrow();

        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new UnauthorizedActionException(
                    "Only Admin can create notices"
            );
        }

        Notice notice = Notice.builder()
                .siteId(user.getSiteId())
                .title(request.getTitle())
                .message(request.getMessage())
                .active(true)
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .build();

        Notice savedNotice = noticeRepo.save(notice);

        List<User> users = userRepo.findBySiteId(user.getSiteId());

        for (User resident : users) {
            notificationService.notifyUser(
                    resident.getUserId(),
                    user.getSiteId(),
                    request.getTitle(),
                    request.getMessage(),
                    "NOTICE"
            );
        }

        return savedNotice;
    }

    public Notice toggleNotice(UUID userId, UUID noticeId) {
        User user = userRepo.findById(userId).orElseThrow();

        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new UnauthorizedActionException(
                    "Only Admin can update notices"
            );
        }

        Notice notice = noticeRepo.findById(noticeId).orElseThrow();

        if (!notice.getSiteId().equals(user.getSiteId())) {
            throw new UnauthorizedActionException("Invalid notice");
        }

        notice.setActive(!Boolean.TRUE.equals(notice.getActive()));

        return noticeRepo.save(notice);
    }
}