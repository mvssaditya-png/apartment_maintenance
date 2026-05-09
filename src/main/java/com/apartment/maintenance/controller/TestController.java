package com.apartment.maintenance.controller;

import com.apartment.maintenance.security.SecurityUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class TestController {

    @GetMapping("/api/test")
    public String test() {

        UUID userId = SecurityUtil.getCurrentUserId();

        return "Logged User ID: " + userId;
    }
}