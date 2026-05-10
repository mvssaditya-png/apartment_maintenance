package com.apartment.maintenance.controller;

import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/phone/{phone}")
    public Optional<User> getUserByPhone(@PathVariable String phone) {
        return userService.getUserByPhone(phone);
    }

    @GetMapping("/me")
    public Optional<User> getCurrentUser(@AuthenticationPrincipal UUID userId) {

        return userService.getUserById(userId);
    }
}