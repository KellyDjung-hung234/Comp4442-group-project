package com.shareu.auth.api;

import com.shareu.auth.dto.request.AuthLoginRequest;
import com.shareu.auth.dto.request.AuthRegisterRequest;
import com.shareu.auth.dto.request.RequestOtpRequest;
import com.shareu.auth.dto.response.AuthLoginResponse;
import com.shareu.auth.dto.response.AuthRegisterResponse;
import com.shareu.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = {"http://localhost:8089", "http://127.0.0.1:8089", "http://localhost:8080", "http://127.0.0.1:8080"})
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/request-otp")
    public Map<String, String> requestOtp(@Valid @RequestBody RequestOtpRequest request) {
        String otp = authService.requestOtp(request);
        return Map.of("message", "OTP issued", "otp", otp);
    }

    @PostMapping("/forgot-password")
    public Map<String, String> forgotPassword(@Valid @RequestBody RequestOtpRequest request) {
        String otp = authService.requestOtp(request);
        return Map.of("message", "OTP sent for password reset", "otp", otp);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody Map<String, String> body) {
        String email = body.get("email");
        String otp = body.get("otp");
        String newPassword = body.get("newPassword");
        if (email == null || otp == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "email, otp and newPassword are required"));
        }

        authService.resetPassword(email, otp, newPassword);
        return ResponseEntity.ok(Map.of("message", "Password reset successful"));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthRegisterResponse> register(@Valid @RequestBody AuthRegisterRequest request) {
        AuthRegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public AuthLoginResponse login(@Valid @RequestBody AuthLoginRequest request) {
        return authService.login(request);
    }
}
