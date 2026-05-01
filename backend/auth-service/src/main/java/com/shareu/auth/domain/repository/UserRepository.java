package com.shareu.auth.domain.repository;

public interface UserRepository {

    boolean existsById(long userId);

    boolean existsByUsername(String username);

    long create(String username, String password, String email);

    java.util.Optional<UserAuthRecord> findAuthByUsername(String username);

    // Update the stored password for a user (username treated as email/identifier)
    int updatePassword(String username, String newPassword);
}
