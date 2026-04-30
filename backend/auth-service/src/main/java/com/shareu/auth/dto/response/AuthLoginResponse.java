package com.shareu.auth.dto.response;

public record AuthLoginResponse(
        long userId,
        String username,
        String role
) {
}

