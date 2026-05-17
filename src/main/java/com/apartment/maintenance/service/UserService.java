package com.apartment.maintenance.service;

import com.apartment.maintenance.dto.FlatStatementDTO;
import com.apartment.maintenance.dto.LoggedInUserDTO;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    public LoggedInUserDTO getLoggedInUserDetails(UUID userId) {

        List<Object[]> result =
                userRepository.getLoggedInUserDetails(userId);

        if (result.isEmpty()) {
            return null;
        }

        Object[] rows = result.get(0);

        return new LoggedInUserDTO(
                (UUID) rows[0],
                (UUID) rows[1],
                (UUID) rows[2],
                (String) rows[3],
                (String) rows[4],
                (String) rows[5],
                (Boolean) rows[6],
                (String) rows[7],
                (String) rows[8],
                (String) rows[9],
                (LocalDateTime) rows[10]
        );
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