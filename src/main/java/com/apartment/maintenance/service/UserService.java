package com.apartment.maintenance.service;

import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUserByPhone(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public Optional<User> getUserById(UUID userId) {
        return userRepository.findById(userId);
    }

    public User getLoggedInUser() {

        Object principal =
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();

        if (principal == null) {
            throw new RuntimeException("User not authenticated");
        }

        UUID userId = (UUID) principal;

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

}