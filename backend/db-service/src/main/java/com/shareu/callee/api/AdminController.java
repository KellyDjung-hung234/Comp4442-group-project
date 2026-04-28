package com.shareu.callee.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin") 
public class AdminController {

    
    // 1. get the report list 
    @GetMapping("/reports")
    public ResponseEntity<?> getAllReports(@RequestHeader("X-User-Role") String role) {
        // if it is not admin, return access denied 
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }
        return ResponseEntity.ok("Success"); 
    }

    // 2. ban  user
    @PutMapping("/users/{userId}/ban")
    public ResponseEntity<?> banUser(
            @PathVariable long userId,
            @RequestHeader("X-User-Role") String role) {
            
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }
        return ResponseEntity.ok("User Banned"); 
    }
}