package com.shareu.auth.domain.repository;

public record UserAuthRecord(
        long id,
        String username,
        String password
) {
}
