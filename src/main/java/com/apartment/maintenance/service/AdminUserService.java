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

        userRepository.findByPhoneNumber(request.getPhoneNumber())
                .ifPresent(user -> {
                    throw new RuntimeException("Phone number already exists");
                });

        User user = new User();
        user.setSiteId(request.getSiteId());
        user.setFlatId(request.getFlatId());
        user.setFlatNumber(request.getFlatNumber());
        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    public AdminUserResponse updateUser(UUID userId, AdminUserRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setFlatId(request.getFlatId());
        user.setFlatNumber(request.getFlatNumber());

        if (request.getSiteId() != null) {
            user.setSiteId(request.getSiteId());
        }

        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    private AdminUserResponse mapToResponse(User user) {
        return new AdminUserResponse(
                user.getUserId(),
                user.getSiteId(),
                user.getFlatId(),
                user.getFlatNumber(),
                user.getName(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getRole(),
                user.getIsActive()
        );
    }
}