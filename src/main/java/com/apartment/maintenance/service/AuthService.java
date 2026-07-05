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
    private final OtpService otpService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final SmsEventService smsEventService;

    public AuthService(UserRepository userRepository,
                       JwtUtil jwtUtil,
                       SmsEventService smsEventService,
                       OtpService otpService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.smsEventService = smsEventService;
        this.otpService = otpService;
    }

    public boolean sendOtp(String phoneNumber) {

        Optional<User> user =
                userRepository.findByPhoneNumber(phoneNumber);

        if (user.isEmpty()) {
            return false;
        }
        try {

            String otp = otpService.generateOtp(phoneNumber);

            smsEventService.sendLoginOtp(phoneNumber, otp);

            System.out.println("OTP sent: " + otp);

            return true;
        }catch (RuntimeException ex) {

            throw ex;

        }
    }

    public VerifyOtpResponse verifyOtp(String phoneNumber, String otp) {

        otpService.verifyOtp(phoneNumber, otp);

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