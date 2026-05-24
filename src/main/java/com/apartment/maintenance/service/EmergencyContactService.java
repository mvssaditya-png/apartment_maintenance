package com.apartment.maintenance.service;

import com.apartment.maintenance.dto.CreateEmergencyContactRequest;
import com.apartment.maintenance.dto.UpdateEmergencyContactRequest;
import com.apartment.maintenance.entity.EmergencyContact;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.exception.UnauthorizedActionException;
import com.apartment.maintenance.repository.EmergencyContactRepository;
import com.apartment.maintenance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmergencyContactService {

    private final EmergencyContactRepository contactRepo;
    private final UserRepository userRepo;

    public List<EmergencyContact> getContacts(UUID userId) {
        User user = userRepo.findById(userId).orElseThrow();

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            return contactRepo.findBySiteIdOrderByPriorityAscCreatedAtDesc(
                    user.getSiteId()
            );
        }

        return contactRepo.findBySiteIdAndActiveTrueOrderByPriorityAscCreatedAtDesc(
                user.getSiteId()
        );
    }

    public EmergencyContact createContact(
            UUID userId,
            CreateEmergencyContactRequest request
    ) {
        User user = userRepo.findById(userId).orElseThrow();
        validateAdmin(user);

        EmergencyContact contact = EmergencyContact.builder()
                .siteId(user.getSiteId())
                .name(request.getName())
                .role(request.getRole())
                .phoneNumber(request.getPhoneNumber())
                .priority(request.getPriority() != null ? request.getPriority() : 1)
                .active(request.getActive() != null ? request.getActive() : true)
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .build();

        return contactRepo.save(contact);
    }

    public EmergencyContact updateContact(
            UUID userId,
            UUID contactId,
            UpdateEmergencyContactRequest request
    ) {
        User user = userRepo.findById(userId).orElseThrow();
        validateAdmin(user);

        EmergencyContact contact =
                contactRepo.findById(contactId).orElseThrow();

        if (!contact.getSiteId().equals(user.getSiteId())) {
            throw new UnauthorizedActionException("Invalid emergency contact");
        }

        contact.setName(request.getName());
        contact.setRole(request.getRole());
        contact.setPhoneNumber(request.getPhoneNumber());
        contact.setPriority(request.getPriority() != null ? request.getPriority() : 1);
        contact.setActive(request.getActive() != null ? request.getActive() : true);

        return contactRepo.save(contact);
    }

    public EmergencyContact toggleContact(UUID userId, UUID contactId) {
        User user = userRepo.findById(userId).orElseThrow();
        validateAdmin(user);

        EmergencyContact contact =
                contactRepo.findById(contactId).orElseThrow();

        if (!contact.getSiteId().equals(user.getSiteId())) {
            throw new UnauthorizedActionException("Invalid emergency contact");
        }

        contact.setActive(!Boolean.TRUE.equals(contact.getActive()));

        return contactRepo.save(contact);
    }

    private void validateAdmin(User user) {
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new UnauthorizedActionException(
                    "Only Admin can manage emergency contacts"
            );
        }
    }
}