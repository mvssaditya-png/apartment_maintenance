package com.apartment.maintenance.controller;

import com.apartment.maintenance.dto.*;
import com.apartment.maintenance.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        boolean sent =
                authService.sendOtp(request.getPhoneNumber());

        if (!sent) {
            return "User not found";
        }

        return "OTP Sent";
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<VerifyOtpResponse> verifyOtp(
            @RequestBody VerifyOtpRequest request) {

        VerifyOtpResponse response =
                authService.verifyOtp(
                        request.getPhoneNumber(),
                        request.getOtp());

        return ResponseEntity.ok(response);
    }
}
