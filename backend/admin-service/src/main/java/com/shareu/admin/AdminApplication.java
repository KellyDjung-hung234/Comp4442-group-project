package com.shareu.admin;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication(scanBasePackages = "com.shareu")
public class AdminApplication {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Hong_Kong"));
    }

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
