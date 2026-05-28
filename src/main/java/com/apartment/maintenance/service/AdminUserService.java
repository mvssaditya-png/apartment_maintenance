package com.apartment.maintenance.service;

import com.apartment.maintenance.dto.AdminUserRequest;
import com.apartment.maintenance.dto.AdminUserResponse;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.repository.SiteRepository;
import com.apartment.maintenance.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final SiteRepository siteRepository;

    public AdminUserService(UserRepository userRepository, SiteRepository siteRepository) {
        this.userRepository = userRepository;
        this.siteRepository = siteRepository;
    }

    public List<AdminUserResponse> getUsersBySite(UUID siteId) {

        return userRepository.findBySiteId(siteId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public AdminUserResponse createUser(AdminUserRequest request) {

        if (!siteRepository.existsById(request.getSiteId())) {
            throw new RuntimeException("Site not found");
        }

        if (request.getEmail() != null
                && !request.getEmail().trim().isEmpty()
                && !request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {

            throw new RuntimeException("Invalid email format");
        }

        userRepository.findByPhoneNumber(request.getPhoneNumber())
                .ifPresent(existingUser -> {
                    throw new RuntimeException("Phone number already exists");
                });

        User user = new User();

        user.setSiteId(request.getSiteId());
        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(
                request.getEmail() != null &&
                        !request.getEmail().trim().isEmpty()
                        ? request.getEmail().trim()
                        : null
        );
        user.setRole(request.getRole());
        user.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        applyResidentRelationship(user, request);

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    public AdminUserResponse updateUser(UUID userId, AdminUserRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getPhoneNumber() != null
                && !request.getPhoneNumber().equals(user.getPhoneNumber())) {

            userRepository.findByPhoneNumber(request.getPhoneNumber())
                    .ifPresent(existingUser -> {
                        throw new RuntimeException("Phone number already exists");
                    });
        }

        if (request.getEmail() != null
                && !request.getEmail().trim().isEmpty()
                && !request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {

            throw new RuntimeException("Invalid email format");
        }

        if (request.getSiteId() != null) {
            if (!siteRepository.existsById(request.getSiteId())) {
                throw new RuntimeException("Site not found");
            }

            user.setSiteId(request.getSiteId());
        }

        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(
                request.getEmail() != null &&
                        !request.getEmail().trim().isEmpty()
                        ? request.getEmail().trim()
                        : null
        );
        user.setRole(request.getRole());

        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }

        applyResidentRelationship(user, request);

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    private void applyResidentRelationship(User user, AdminUserRequest request) {

        String residentType = request.getResidentType();

        if (residentType == null || residentType.isBlank()) {
            user.setResidentType(null);
            user.setOwnerUserId(null);
            user.setFlatId(request.getFlatId());
            user.setFlatNumber(request.getFlatNumber());
            return;
        }

        if ("OWNER".equalsIgnoreCase(residentType)) {
            user.setResidentType("OWNER");
            user.setOwnerUserId(null);
            user.setFlatId(request.getFlatId());
            user.setFlatNumber(request.getFlatNumber());
            return;
        }

        if ("TENANT".equalsIgnoreCase(residentType)) {
            if (request.getOwnerUserId() == null) {
                throw new RuntimeException("Owner is required for tenant");
            }

            User owner = userRepository.findById(request.getOwnerUserId())
                    .orElseThrow(() -> new RuntimeException("Owner not found"));

            if (!"OWNER".equalsIgnoreCase(owner.getResidentType())) {
                throw new RuntimeException("Selected user is not an owner");
            }

            if (user.getUserId() != null && owner.getUserId().equals(user.getUserId())) {
                throw new RuntimeException("Tenant cannot be owner of self");
            }

            if (request.getSiteId() != null
                    && owner.getSiteId() != null
                    && !owner.getSiteId().equals(request.getSiteId())) {
                throw new RuntimeException("Selected owner belongs to another site");
            }

            user.setResidentType("TENANT");
            user.setOwnerUserId(owner.getUserId());
            user.setFlatId(owner.getFlatId());
            user.setFlatNumber(owner.getFlatNumber());
            return;
        }

        throw new RuntimeException("Invalid resident type");
    }

    private AdminUserResponse mapToResponse(User user) {

        String ownerName = null;
        String ownerPhoneNumber = null;

        if (user.getOwnerUserId() != null) {

            userRepository.findById(user.getOwnerUserId())
                    .ifPresent(owner -> {
                        // cannot assign local vars directly inside lambda
                    });

            User owner = userRepository.findById(user.getOwnerUserId())
                    .orElse(null);

            if (owner != null) {
                ownerName = owner.getName();
                ownerPhoneNumber = owner.getPhoneNumber();
            }
        }

        return new AdminUserResponse(
                user.getUserId(),
                user.getSiteId(),
                user.getFlatId(),
                user.getFlatNumber(),
                user.getName(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getRole(),
                user.getIsActive(),
                user.getResidentType(),
                user.getOwnerUserId(),
                ownerName,
                ownerPhoneNumber
        );
    }
    public List<AdminUserResponse> getOwnersBySite(UUID siteId) {
        return userRepository
                .findBySiteIdAndResidentTypeAndIsActive(siteId, "OWNER", true)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

}