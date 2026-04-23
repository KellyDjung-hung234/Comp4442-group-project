package com.shareu.callee.domain.repository;

public interface UserRepository {

    boolean existsById(long userId);

    boolean existsByUsername(String username);

    long create(String username, String displayName, String password);

    java.util.Optional<UserAuthRecord> findAuthByUsername(String username);
}
