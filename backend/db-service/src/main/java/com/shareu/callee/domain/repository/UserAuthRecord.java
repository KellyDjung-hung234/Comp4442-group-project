package com.shareu.callee.domain.repository;

public record UserAuthRecord(
        long id,
        String username,
        String password
) {
}
