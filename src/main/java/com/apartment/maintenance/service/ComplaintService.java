package com.apartment.maintenance.service;

import com.apartment.maintenance.dto.CreateComplaintRequest;
import com.apartment.maintenance.dto.UpdateComplaintStatusRequest;
import com.apartment.maintenance.entity.Complaint;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.exception.UnauthorizedActionException;
import com.apartment.maintenance.repository.ComplaintRepository;
import com.apartment.maintenance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepo;
    private final UserRepository userRepo;
    private final NotificationService notificationService;

    public List<Complaint> getComplaints(UUID userId) {

        User user = userRepo.findById(userId).orElseThrow();

        if ("ADMIN".equalsIgnoreCase(user.getRole())
                || "CASHIER".equalsIgnoreCase(user.getRole())) {
            return complaintRepo.findBySiteIdOrderByCreatedAtDesc(
                    user.getSiteId()
            );
        }

        return complaintRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Complaint createComplaint(
            UUID userId,
            CreateComplaintRequest request
    ) {

        User user = userRepo.findById(userId).orElseThrow();

        Complaint complaint = Complaint.builder()
                .siteId(user.getSiteId())
                .flatId(user.getFlatId())
                .userId(userId)
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .status("OPEN")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Complaint savedComplaint = complaintRepo.save(complaint);

        List<User> adminsAndCashiers =
                userRepo.findBySiteIdAndRoleIn(
                        user.getSiteId(),
                        List.of("ADMIN", "CASHIER")
                );

        for (User receiver : adminsAndCashiers) {
            notificationService.notifyUser(
                    receiver.getUserId(),
                    user.getSiteId(),
                    "New Complaint Raised",
                    "Flat " + user.getFlatNumber()
                            + " raised a complaint: "
                            + request.getTitle(),
                    "COMPLAINT"
            );
        }

        return savedComplaint;
    }

    public Complaint updateComplaintStatus(
            UUID userId,
            UUID complaintId,
            UpdateComplaintStatusRequest request
    ) {

        User user = userRepo.findById(userId).orElseThrow();

        if (!"ADMIN".equalsIgnoreCase(user.getRole())
                && !"CASHIER".equalsIgnoreCase(user.getRole())) {
            throw new UnauthorizedActionException(
                    "Only Admin or Cashier can update complaints"
            );
        }

        Complaint complaint =
                complaintRepo.findById(complaintId).orElseThrow();

        if (!complaint.getSiteId().equals(user.getSiteId())) {
            throw new UnauthorizedActionException("Invalid complaint");
        }

        validateStatus(request.getStatus());

        complaint.setStatus(request.getStatus());
        complaint.setAdminResponse(request.getAdminResponse());
        complaint.setUpdatedAt(LocalDateTime.now());

        Complaint updatedComplaint = complaintRepo.save(complaint);

        notificationService.notifyUser(
                complaint.getUserId(),
                complaint.getSiteId(),
                "Complaint Updated",
                "Your complaint \"" + complaint.getTitle()
                        + "\" status has been updated to "
                        + request.getStatus()
                        + ".",
                "COMPLAINT_UPDATED"
        );

        return updatedComplaint;
    }

    private void validateStatus(String status) {
        if (!"OPEN".equalsIgnoreCase(status)
                && !"IN_PROGRESS".equalsIgnoreCase(status)
                && !"RESOLVED".equalsIgnoreCase(status)
                && !"REJECTED".equalsIgnoreCase(status)) {
            throw new RuntimeException("Invalid complaint status");
        }
    }
}