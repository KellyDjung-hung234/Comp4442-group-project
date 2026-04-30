package com.shareu.reaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.shareu")
public class ReactionApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactionApplication.class, args);
    }
}
