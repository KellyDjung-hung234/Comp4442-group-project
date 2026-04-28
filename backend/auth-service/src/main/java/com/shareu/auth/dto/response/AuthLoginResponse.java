package com.shareu.auth.dto.response;

public record AuthLoginResponse(
        Long userId,
        String username
) {
}
