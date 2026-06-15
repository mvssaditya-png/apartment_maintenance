package com.apartment.maintenance.service;
import com.apartment.maintenance.dto.AuthResponse;
import com.apartment.maintenance.dto.VerifyOtpResponse;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.exception.UnauthorizedActionException;
import com.apartment.maintenance.repository.UserRepository;
import com.apartment.maintenance.security.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // Step 1: Send OTP (Mock)
    public boolean sendOtp(String phoneNumber) {

        Optional<User> user =
                userRepository.findByPhoneNumber(phoneNumber);

        if (user.isEmpty()) {
            return false;
        }

        System.out.println("OTP sent: 123456");

        return true;
    }

    // Step 2: Verify OTP
    public VerifyOtpResponse verifyOtp(String phoneNumber, String otp) {

        // TEMP OTP CHECK (for testing)
        if (!"123456".equals(otp)) {
            throw new UnauthorizedActionException("Invalid OTP");
        }

        User user = userRepository
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getUserId());

        return new VerifyOtpResponse(
                token,
                user.getUserId(),
                user.getRole(),
                "Login Successful"
        );
    }
}