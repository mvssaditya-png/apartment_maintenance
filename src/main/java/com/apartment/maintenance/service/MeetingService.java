package com.apartment.maintenance.service;

import com.apartment.maintenance.dto.CreateMeetingRequest;
import com.apartment.maintenance.dto.UpdateMeetingRequest;
import com.apartment.maintenance.entity.Meeting;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.exception.UnauthorizedActionException;
import com.apartment.maintenance.repository.MeetingRepository;
import com.apartment.maintenance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepo;
    private final UserRepository userRepo;
    private final NotificationService notificationService;
    private final SmsEventService smsEventService;
    public List<Meeting> getMeetings(UUID userId) {

        User user = userRepo.findById(userId).orElseThrow();

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            return meetingRepo.findBySiteIdOrderByMeetingDateDesc(
                    user.getSiteId()
            );
        }

        return meetingRepo.findBySiteIdAndActiveTrueOrderByMeetingDateDesc(
                user.getSiteId()
        );
    }

    public Meeting createMeeting(
            UUID userId,
            CreateMeetingRequest request
    ) {

        User user = userRepo.findById(userId).orElseThrow();

        validateAdmin(user);

        Meeting meeting = Meeting.builder()
                .siteId(user.getSiteId())
                .title(request.getTitle())
                .description(request.getDescription())
                .meetingDate(request.getMeetingDate())
                .location(request.getLocation())
                .status("UPCOMING")
                .active(true)
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .build();

        Meeting savedMeeting = meetingRepo.save(meeting);

        List<User> users = userRepo.findBySiteId(user.getSiteId());

        String meetingDateText = request.getMeetingDate()
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));

        for (User member : users) {
            notificationService.notifyUser(
                    member.getUserId(),
                    user.getSiteId(),
                    "New Meeting Scheduled",
                    "Meeting \"" + request.getTitle()
                            + "\" is scheduled on "
                            + meetingDateText
                            + (request.getLocation() != null && !request.getLocation().isBlank()
                            ? " at " + request.getLocation()
                            : "")
                            + ".",
                    "MEETING"
            );
            smsEventService.meetingCreated(member, savedMeeting);
        }

        return savedMeeting;
    }

    public Meeting updateMeeting(
            UUID userId,
            UUID meetingId,
            UpdateMeetingRequest request
    ) {

        User user = userRepo.findById(userId).orElseThrow();

        validateAdmin(user);

        Meeting meeting = meetingRepo.findById(meetingId).orElseThrow();

        if (!meeting.getSiteId().equals(user.getSiteId())) {
            throw new UnauthorizedActionException("Invalid meeting");
        }

        meeting.setTitle(request.getTitle());
        meeting.setDescription(request.getDescription());
        meeting.setMeetingDate(request.getMeetingDate());
        meeting.setLocation(request.getLocation());

        if (request.getActive() != null) {
            meeting.setActive(request.getActive());
        }
        if (request.getStatus() != null) {
            validateStatus(request.getStatus());
            meeting.setStatus(request.getStatus());
        }
        return meetingRepo.save(meeting);
    }

    private void validateStatus(String status) {
        if (!status.equalsIgnoreCase("UPCOMING")
                && !status.equalsIgnoreCase("ONGOING")
                && !status.equalsIgnoreCase("COMPLETED")
                && !status.equalsIgnoreCase("CANCELLED")) {
            throw new RuntimeException("Invalid meeting status");
        }
    }

    public Meeting toggleMeeting(UUID userId, UUID meetingId) {

        User user = userRepo.findById(userId).orElseThrow();

        validateAdmin(user);

        Meeting meeting = meetingRepo.findById(meetingId).orElseThrow();

        if (!meeting.getSiteId().equals(user.getSiteId())) {
            throw new UnauthorizedActionException("Invalid meeting");
        }

        meeting.setActive(!Boolean.TRUE.equals(meeting.getActive()));

        return meetingRepo.save(meeting);
    }

    private void validateAdmin(User user) {

        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new UnauthorizedActionException(
                    "Only Admin can manage meetings"
            );
        }
    }
}