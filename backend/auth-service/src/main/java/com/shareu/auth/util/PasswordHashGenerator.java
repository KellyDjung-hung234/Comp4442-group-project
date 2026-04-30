package com.shareu.auth.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        String[] pwds = new String[]{"cheesering", "kwanloan", "kelly", "admin123"};
        for (String p : pwds) {
            System.out.println(p + ":" + enc.encode(p));
        }
    }
}
