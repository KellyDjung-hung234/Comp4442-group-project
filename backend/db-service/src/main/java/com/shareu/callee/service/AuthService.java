package com.shareu.callee.service;

import com.shareu.callee.domain.repository.UserAuthRecord;
import com.shareu.callee.domain.repository.UserRepository;
import com.shareu.callee.dto.request.AuthLoginRequest;
import com.shareu.callee.dto.request.AuthRegisterRequest;
import com.shareu.callee.dto.request.RequestOtpRequest;
import com.shareu.callee.dto.response.AuthLoginResponse;
import com.shareu.callee.dto.response.AuthRegisterResponse;
import com.shareu.callee.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public String requestOtp(RequestOtpRequest request) {
        String username = request.username().trim().toLowerCase();
        String otp = "123456";
        otpStore.put(username, otp);
        return otp;
    }

    @Transactional
    public AuthRegisterResponse register(AuthRegisterRequest request) {
        String username = request.username().trim().toLowerCase();

        if (!request.termsAccepted()) {
            throw new BadRequestException("Terms of use must be accepted");
        }

        String savedOtp = otpStore.get(username);
        if (savedOtp == null || !savedOtp.equals(request.otp())) {
            throw new BadRequestException("Invalid OTP");
        }

        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Username already exists");
        }

        long userId = userRepository.create(username, username, request.password());
        otpStore.remove(username);

        return new AuthRegisterResponse(userId, username);
    }

    @Transactional(readOnly = true)
    public AuthLoginResponse login(AuthLoginRequest request) {
        String username = request.username().trim().toLowerCase();
        UserAuthRecord user = userRepository.findAuthByUsername(username)
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));

        if (!user.password().equals(request.password())) {
            throw new BadRequestException("Invalid username or password");
        }

        return new AuthLoginResponse(user.id(), user.username());
    }
}
