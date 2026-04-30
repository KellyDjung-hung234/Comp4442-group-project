package com.shareu.auth.dto.response;

public record AuthRegisterResponse(
        Long userId,
        String username
) {
}
