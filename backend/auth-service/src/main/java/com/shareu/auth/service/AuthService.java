package com.shareu.auth.service;

import com.shareu.auth.domain.repository.UserAuthRecord;
import com.shareu.auth.domain.repository.UserRepository;
import com.shareu.auth.dto.request.AuthLoginRequest;
import com.shareu.auth.dto.request.AuthRegisterRequest;
import com.shareu.auth.dto.request.RequestOtpRequest;
import com.shareu.auth.dto.response.AuthLoginResponse;
import com.shareu.auth.dto.response.AuthRegisterResponse;
import com.shareu.common.exception.BadRequestException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();
    private final JavaMailSender mailSender;
    private final Random random = new Random();
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JavaMailSender mailSender, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public String requestOtp(RequestOtpRequest request) {
        String username = request.username().trim().toLowerCase();
        String recipient = normalizeOptional(request.email());
        if (recipient == null) {
            recipient = username;
        }
        String otp = String.format("%06d", random.nextInt(1_000_000));
        otpStore.put(username, otp);
        otpStore.put(recipient, otp);

        // Always log OTP to console for development/testing
        log.info("Mock Email - OTP for {} is: {}", recipient, otp);

        // Try to send email if mailSender configured, but don't fail if it doesn't work
        try {
            if (mailSender != null) {
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setTo(recipient);
                msg.setSubject("Your ShareU OTP");
                msg.setText("Your ShareU OTP is: " + otp + "\nThis code is valid for a short time.");
                mailSender.send(msg);
                log.info("OTP email sent successfully to {}", recipient);
            }
        } catch (Exception e) {
            log.error("Failed to send email for OTP, but proceeding with registration. OTP: {}", otp, e);
        }

        return otp;
    }

    @Transactional(readOnly = true)
    public boolean validateOtp(String username, String otp) {
        String key = username.trim().toLowerCase();
        String saved = otpStore.get(key);
        return saved != null && saved.equals(otp);
    }

    @Transactional
    public void invalidateOtp(String username) {
        otpStore.remove(username.trim().toLowerCase());
    }

    @Transactional
    public AuthRegisterResponse register(AuthRegisterRequest request) {
        String username = request.username().trim().toLowerCase();
        String email = normalizeOptional(request.email());

        if (!request.termsAccepted()) {
            throw new BadRequestException("Terms of use must be accepted");
        }

        String otpKey = email != null ? email : username;
        String savedOtp = otpStore.get(otpKey);
        if (savedOtp == null) {
            savedOtp = otpStore.get(username);
        }
        if (savedOtp == null || !savedOtp.equals(request.otp())) {
            throw new BadRequestException("Invalid OTP");
        }

        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Username already exists");
        }

        String encoded = passwordEncoder.encode(request.password());
        long userId = userRepository.create(username, encoded, email);
        otpStore.remove(username);
        if (email != null) {
            otpStore.remove(email);
        }

        return new AuthRegisterResponse(userId, username);
    }

    @Transactional(readOnly = true)
    public AuthLoginResponse login(AuthLoginRequest request) {
        String username = request.username().trim().toLowerCase();
        UserAuthRecord user = userRepository.findAuthByUsername(username)
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));

        if (user.banned()) {
            throw new BadRequestException("This account has been banned");
        }

        if (!passwordEncoder.matches(request.password(), user.password())) {
            throw new BadRequestException("Invalid username or password");
        }

        return new AuthLoginResponse(user.id(), user.username(), user.role());
    }

    @Transactional
    public void resetPassword(String username, String otp, String newPassword) {
        String key = username.trim().toLowerCase();
        String saved = otpStore.get(key);
        if (saved == null || !saved.equals(otp)) {
            throw new BadRequestException("Invalid OTP");
        }

        String encodedNew = passwordEncoder.encode(newPassword);
        userRepository.updatePassword(username, encodedNew);
        invalidateOtp(username);
    }

    private String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toLowerCase();
    }
}
