package org.learn.watchwave.subscriptions.controller;

import lombok.RequiredArgsConstructor;
import org.learn.watchwave.subscriptions.dto.request.SubscribeRequest;
import org.learn.watchwave.subscriptions.dto.response.SubscribeResponse;
import org.learn.watchwave.subscriptions.services.SubscribeService;
import org.learn.watchwave.videos.util.AuthenticationHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/me/subscriptions")
@RequiredArgsConstructor
public class SubscribeController {

    private final SubscribeService subscriptionService;
    private final AuthenticationHelper authHelper;

    @PostMapping
    public ResponseEntity<SubscribeResponse> subscribe(
            @RequestBody SubscribeRequest request,
            Authentication authentication) {
        UUID subscriberId = authHelper.extractUserId(authentication);
        SubscribeResponse response = subscriptionService.subscribe(subscriberId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{creatorId}")
    public ResponseEntity<Void> unsubscribe(
            @PathVariable UUID creatorId,
            Authentication authentication) {
        UUID subscriberId = authHelper.extractUserId(authentication);
        subscriptionService.unsubscribe(subscriberId, creatorId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SubscribeResponse>> getSubscriptions(Authentication authentication) {
        UUID subscriberId = authHelper.extractUserId(authentication);
        return ResponseEntity.ok(subscriptionService.getSubscriptions(subscriberId));
    }

    @GetMapping("/to-me")
    public ResponseEntity<List<SubscribeResponse>> getSubscribers(Authentication authentication) {
        UUID creatorId = authHelper.extractUserId(authentication);
        return ResponseEntity.ok(subscriptionService.getSubscribers(creatorId));
    }

    @GetMapping("/contains/{creatorId}")
    public ResponseEntity<Boolean> isSubscribed(
            @PathVariable UUID creatorId,
            Authentication authentication) {
        UUID subscriberId = authHelper.extractUserId(authentication);
        return ResponseEntity.ok(subscriptionService.isSubscribed(subscriberId, creatorId));
    }
}