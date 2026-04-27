package com.shareu.callee.api;

import com.shareu.callee.dto.request.AuthLoginRequest;
import com.shareu.callee.dto.request.AuthRegisterRequest;
import com.shareu.callee.dto.request.RequestOtpRequest;
import com.shareu.callee.dto.response.AuthLoginResponse;
import com.shareu.callee.dto.response.AuthRegisterResponse;
import com.shareu.callee.service.AuthService;
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
@CrossOrigin(origins = {"http://localhost:8080", "http://127.0.0.1:8080"})
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
