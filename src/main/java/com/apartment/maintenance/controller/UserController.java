package com.apartment.maintenance.controller;

import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
}