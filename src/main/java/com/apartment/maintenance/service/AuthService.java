package com.apartment.maintenance.service;

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
    private final SmsEventService smsEventService;

    public AuthService(UserRepository userRepository,
                       JwtUtil jwtUtil,
                       SmsEventService smsEventService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.smsEventService = smsEventService;
    }

    public boolean sendOtp(String phoneNumber) {

        Optional<User> user =
                userRepository.findByPhoneNumber(phoneNumber);

        if (user.isEmpty()) {
            return false;
        }

        String otp = "123456";

        smsEventService.sendLoginOtp(phoneNumber, otp);

        System.out.println("OTP sent: " + otp);

        return true;
    }

    public VerifyOtpResponse verifyOtp(String phoneNumber, String otp) {

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