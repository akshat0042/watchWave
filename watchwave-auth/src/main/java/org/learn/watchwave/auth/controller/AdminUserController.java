package org.learn.watchwave.auth.controller;

import org.learn.watchwave.auth.model.entity.User;
import org.learn.watchwave.auth.service.interfaces.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        Page<User> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable UUID userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{userId}/block")
    public ResponseEntity<String> blockUser(@PathVariable UUID userId) {
        userService.blockUser(userId);
        return ResponseEntity.ok("User blocked successfully");
    }

    @PatchMapping("/{userId}/unblock")
    public ResponseEntity<String> unblockUser(@PathVariable UUID userId) {
        userService.unblockUser(userId);
        return ResponseEntity.ok("User unblocked successfully");
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<Page<User>> searchUsers(
            @RequestParam String query,
            Pageable pageable) {
        Page<User> users = userService.searchUsers(query, pageable);
        return ResponseEntity.ok(users);
    }
}
