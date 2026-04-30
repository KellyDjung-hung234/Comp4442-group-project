package com.shareu.admin.api;

import com.shareu.common.exception.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = {"http://localhost:8089", "http://127.0.0.1:8089", "http://localhost:8080", "http://127.0.0.1:8080"})
public class AdminController {

    private final JdbcTemplate jdbcTemplate;

    public AdminController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/admin/dashboard-stats")
    public Map<String, Object> getDashboardStats() {
        Long totalMembers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Long.class);
        Long totalPosts = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM topics WHERE is_deleted = FALSE", Long.class);
        Long totalReports = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM reports", Long.class);

        return Map.of(
                "totalMembers", totalMembers == null ? 0L : totalMembers,
                "totalPosts", totalPosts == null ? 0L : totalPosts,
                "totalReports", totalReports == null ? 0L : totalReports,
                "ec2Status", "Running",
                "networkTraffic", List.of(12, 19, 3, 5, 2, 3, 15),
                "recentAuditLogs", getRecentAuditLogs()
        );
    }

    @GetMapping("/admin/reports")
    public ResponseEntity<?> getAllReports(@RequestHeader("X-User-Role") String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        try {
            List<Map<String, Object>> reports = jdbcTemplate.queryForList(
                    "SELECT r.id, r.target_type, r.target_id, r.reason, r.details, r.reported_by, r.created_by, r.created_at, " +
                            "COALESCE(r.status, 'OPEN') AS status, r.handled_at, " +
                            "u.username AS reporter_username " +
                            "FROM reports r LEFT JOIN users u ON u.id = COALESCE(r.reported_by, r.created_by) " +
                            "ORDER BY r.created_at DESC"
            ).stream().map(report -> {
                Map<String, Object> enriched = new java.util.LinkedHashMap<>(report);
                String targetType = report.get("target_type") == null ? "" : String.valueOf(report.get("target_type"));
                Long targetId = report.get("target_id") instanceof Number number ? number.longValue() : null;
                Map<String, Object> targetDetails = fetchReportTargetDetails(targetType, targetId);
                enriched.put("targetContent", targetDetails.get("targetContent"));
                enriched.put("targetAuthor", targetDetails.get("targetAuthor"));
                enriched.put("targetAuthorId", targetDetails.get("targetAuthorId"));
                enriched.put("targetDeleted", targetDetails.get("targetDeleted"));
                return enriched;
            }).toList();
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch reports: " + e.getMessage()));
        }
    }

    @GetMapping("/admin/users")
    public ResponseEntity<?> getAllUsers(@RequestHeader("X-User-Role") String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        try {
            List<Map<String, Object>> users = jdbcTemplate.queryForList(
                    "SELECT u.id, u.username, COALESCE(u.email, '') AS email, u.role, u.created_at, COALESCE(u.is_banned, FALSE) AS is_banned, " +
                            "(SELECT COUNT(*) FROM topics t WHERE t.created_by = u.id AND t.is_deleted = FALSE) AS post_count, " +
                            "(SELECT COUNT(*) FROM comments c WHERE c.created_by = u.id AND c.is_deleted = FALSE) AS comment_count " +
                            "FROM users u ORDER BY u.created_at DESC"
            );
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch users: " + e.getMessage()));
        }
    }

    @GetMapping("/admin/users/{userId}/activity")
    public ResponseEntity<?> getUserActivity(
            @PathVariable long userId,
            @RequestHeader("X-User-Role") String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        try {
            List<Map<String, Object>> users = jdbcTemplate.queryForList(
                    "SELECT id, username, COALESCE(email, '') AS email, role, COALESCE(is_banned, FALSE) AS is_banned " +
                            "FROM users WHERE id = ?",
                    userId
            );
            if (users.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
            }

            List<Map<String, Object>> posts = jdbcTemplate.queryForList(
                    "SELECT id, title, comment_count, created_at, updated_at, COALESCE(is_deleted, FALSE) AS is_deleted, deleted_at " +
                            "FROM topics WHERE created_by = ? ORDER BY created_at DESC",
                    userId
            );
            List<Map<String, Object>> comments = jdbcTemplate.queryForList(
                    "SELECT c.id, c.topic_id, c.text_content, c.created_at, c.updated_at, COALESCE(c.is_deleted, FALSE) AS is_deleted, c.deleted_at, " +
                            "t.title AS topic_title, COALESCE(t.is_deleted, FALSE) AS topic_deleted " +
                            "FROM comments c LEFT JOIN topics t ON t.id = c.topic_id " +
                            "WHERE c.created_by = ? ORDER BY c.created_at DESC",
                    userId
            );
            List<Map<String, Object>> banHistory = jdbcTemplate.queryForList(
                    "SELECT id, action, details, action_by, created_at " +
                            "FROM audit_logs " +
                            "WHERE action IN ('BAN_USER', 'UNBAN_USER') AND details LIKE ? " +
                            "ORDER BY created_at DESC",
                    "%user id=" + userId + "%"
            );

            Map<String, Object> user = users.get(0);
            return ResponseEntity.ok(Map.of(
                    "user", user,
                    "posts", posts,
                    "comments", comments,
                    "banHistory", banHistory
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch user activity: " + e.getMessage()));
        }
    }

    @GetMapping("/admin/notifications/unread")
    public ResponseEntity<?> getUnreadNotifications(@RequestHeader("X-User-Id") long userId) {
        try {
            List<Map<String, Object>> notifications = jdbcTemplate.queryForList(
                    "SELECT id, user_id, message, is_read, created_at FROM notifications WHERE user_id = ? AND is_read = FALSE ORDER BY created_at DESC",
                    userId
            );
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch notifications: " + e.getMessage()));
        }
    }

    @PostMapping("/admin/notifications/{notificationId}/read")
    public ResponseEntity<?> markNotificationRead(
            @PathVariable long notificationId,
            @RequestHeader("X-User-Id") long userId) {
        try {
            int updated = jdbcTemplate.update(
                    "UPDATE notifications SET is_read = TRUE WHERE id = ? AND user_id = ?",
                    notificationId,
                    userId
            );
            if (updated > 0) {
                return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Notification not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update notification: " + e.getMessage()));
        }
    }

    @DeleteMapping("/admin/content/{type}/{id}")
    public ResponseEntity<?> deleteContent(
            @PathVariable String type,
            @PathVariable long id,
            @RequestHeader("X-User-Role") String role) {

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        try {
            if ("topic".equalsIgnoreCase(type)) {
                Long authorId = null;
                try {
                    authorId = jdbcTemplate.queryForObject("SELECT created_by FROM topics WHERE id = ?", Long.class, id);
                } catch (Exception ignored) {
                }
                
                int deleted = jdbcTemplate.update(
                        "UPDATE topics SET is_deleted = TRUE, deleted_at = NOW() WHERE id = ? AND is_deleted = FALSE",
                        id
                );
                if (deleted > 0) {
                    insertAuditLog("DELETE_TOPIC", "Soft deleted topic id=" + id);
                    
                    if (authorId != null) {
                        insertNotification(authorId, "Your post has been removed by an administrator for violating community guidelines.");
                    }
                    
                    return ResponseEntity.ok(Map.of("message", "Topic soft deleted successfully"));
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Topic not found");
            }
            if ("comment".equalsIgnoreCase(type)) {
                Long authorId = null;
                Long topicId = null;
                try {
                    authorId = jdbcTemplate.queryForObject("SELECT created_by FROM comments WHERE id = ?", Long.class, id);
                    topicId = jdbcTemplate.queryForObject("SELECT topic_id FROM comments WHERE id = ?", Long.class, id);
                } catch (Exception ignored) {
                }
                
                int deleted = jdbcTemplate.update(
                        "UPDATE comments SET is_deleted = TRUE, deleted_at = NOW() WHERE id = ? AND is_deleted = FALSE",
                        id
                );
                if (deleted > 0) {
                    insertAuditLog("DELETE_COMMENT", "Soft deleted comment id=" + id);
                    
                    if (topicId != null) {
                        jdbcTemplate.update(
                                "UPDATE topics SET comment_count = GREATEST(comment_count - 1, 0) WHERE id = ?",
                                topicId
                        );
                    }

                    if (authorId != null) {
                        insertNotification(authorId, "Your comment has been removed by an administrator for violating community guidelines.");
                    }
                    
                    return ResponseEntity.ok(Map.of("message", "Comment soft deleted successfully"));
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found");
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid content type. Must be 'topic' or 'comment'"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete content: " + e.getMessage()));
        }
    }

    @RequestMapping(path = {"/admin/reports/{reportId}/resolve", "/admin/reports/{reportId}/close"}, method = RequestMethod.POST)
    public ResponseEntity<?> resolveReport(
            @PathVariable long reportId,
            @RequestHeader("X-User-Role") String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        try {
            int updated = jdbcTemplate.update(
                    "UPDATE reports SET status = 'CLOSED', handled_at = NOW() WHERE id = ? AND COALESCE(status, 'OPEN') <> 'CLOSED'",
                    reportId
            );
            if (updated > 0) {
                insertAuditLog("CLOSE_REPORT", "Closed report id=" + reportId);
                return ResponseEntity.ok(Map.of("message", "Report closed successfully"));
            }

            Long exists = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM reports WHERE id = ?", Long.class, reportId);
            if (exists != null && exists > 0) {
                return ResponseEntity.ok(Map.of("message", "Report already closed"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Report not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to resolve report: " + e.getMessage()));
        }
    }

    @PutMapping("/admin/users/{userId}/ban")
    public ResponseEntity<?> banUser(
            @PathVariable long userId,
            @RequestHeader("X-User-Role") String role) {
        ensureTargetUserIsNotAdmin(userId);
        return updateBanStatus(userId, true, role);
    }

    @PostMapping("/admin/users/{userId}/ban")
    public ResponseEntity<?> postBanUser(
            @PathVariable long userId,
            @RequestHeader("X-User-Role") String role) {
        ensureTargetUserIsNotAdmin(userId);
        return updateBanStatus(userId, true, role);
    }

    @PostMapping("/admin/users/{userId}/unban")
    public ResponseEntity<?> unbanUser(
            @PathVariable long userId,
            @RequestHeader("X-User-Role") String role) {
        return updateBanStatus(userId, false, role);
    }

    private ResponseEntity<?> updateBanStatus(long userId, boolean banned, String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        try {
            int updated = jdbcTemplate.update("UPDATE users SET is_banned = ? WHERE id = ?", banned, userId);
            if (updated > 0) {
                insertAuditLog(banned ? "BAN_USER" : "UNBAN_USER", (banned ? "Banned" : "Unbanned") + " user id=" + userId);
                return ResponseEntity.ok(Map.of("message", banned ? "User banned successfully" : "User unbanned successfully"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update user status: " + e.getMessage()));
        }
    }

    private List<Map<String, Object>> getRecentAuditLogs() {
        try {
            return jdbcTemplate.queryForList(
                    "SELECT id, action, details, action_by, created_at FROM audit_logs ORDER BY created_at DESC LIMIT 10"
            );
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void insertAuditLog(String action, String details) {
        String actionBy = currentAdminUsername();
        jdbcTemplate.update(
                "INSERT INTO audit_logs (action, details, action_by) VALUES (?, ?, ?)",
                action,
                details,
                actionBy
        );
    }

    private void insertNotification(Long userId, String message) {
        try {
            jdbcTemplate.update(
                    "INSERT INTO notifications (user_id, message, is_read) VALUES (?, ?, FALSE)",
                    userId,
                    message
            );
        } catch (Exception e) {
            // Log silently to avoid disrupting the delete operation
        }
    }

    private Map<String, Object> fetchReportTargetDetails(String targetType, Long targetId) {
        if (targetId == null || targetType == null || targetType.isBlank()) {
            return Map.of(
                    "targetContent", "[Content Deleted]",
                    "targetAuthor", "[Content Deleted]",
                    "targetAuthorId", "",
                    "targetDeleted", true
            );
        }

        try {
            if ("POST".equalsIgnoreCase(targetType) || "TOPIC".equalsIgnoreCase(targetType)) {
                List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                        "SELECT t.title AS content, t.created_by AS author_id, COALESCE(t.is_deleted, FALSE) AS is_deleted, u.username AS author_username " +
                                "FROM topics t LEFT JOIN users u ON u.id = t.created_by WHERE t.id = ?",
                        targetId
                );
                if (!rows.isEmpty()) {
                    Map<String, Object> row = rows.get(0);
                    return Map.of(
                            "targetContent", valueOrDefault(row.get("content"), "[Content Deleted]"),
                            "targetAuthor", valueOrDefault(row.get("author_username"), "[Content Deleted]"),
                            "targetAuthorId", valueOrDefault(row.get("author_id"), ""),
                            "targetDeleted", isTruthy(row.get("is_deleted"))
                    );
                }
            }

            if ("COMMENT".equalsIgnoreCase(targetType)) {
                List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                        "SELECT c.text_content AS content, c.created_by AS author_id, COALESCE(c.is_deleted, FALSE) AS is_deleted, u.username AS author_username " +
                                "FROM comments c LEFT JOIN users u ON u.id = c.created_by WHERE c.id = ?",
                        targetId
                );
                if (!rows.isEmpty()) {
                    Map<String, Object> row = rows.get(0);
                    return Map.of(
                            "targetContent", valueOrDefault(row.get("content"), "[Content Deleted]"),
                            "targetAuthor", valueOrDefault(row.get("author_username"), "[Content Deleted]"),
                            "targetAuthorId", valueOrDefault(row.get("author_id"), ""),
                            "targetDeleted", isTruthy(row.get("is_deleted"))
                    );
                }
            }
        } catch (Exception ignored) {
        }

        return Map.of(
                "targetContent", "[Content Deleted]",
                "targetAuthor", "[Content Deleted]",
                "targetAuthorId", "",
                "targetDeleted", true
        );
    }

    private void ensureTargetUserIsNotAdmin(long userId) {
        try {
            String targetRole = jdbcTemplate.queryForObject(
                    "SELECT role FROM users WHERE id = ?",
                    String.class,
                    userId
            );
            if ("ADMIN".equalsIgnoreCase(targetRole)) {
                throw new BadRequestException("Cannot ban an administrator.");
            }
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception ignored) {
        }
    }

    private boolean isTruthy(Object value) {
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof Number number) {
            return number.intValue() != 0;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

    private Object valueOrDefault(Object value, Object fallback) {
        return value == null ? fallback : value;
    }

    private String currentAdminUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getName() != null && !authentication.getName().isBlank()) {
            return authentication.getName();
        }
        return "unknown";
    }
}
