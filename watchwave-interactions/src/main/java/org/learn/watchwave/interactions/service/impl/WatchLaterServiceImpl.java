package org.learn.watchwave.interactions.service.impl;

import lombok.RequiredArgsConstructor;
import org.learn.watchwave.interactions.dto.request.WatchLaterRequest;
import org.learn.watchwave.interactions.dto.response.WatchLaterResponse;
import org.learn.watchwave.interactions.model.entity.WatchLater;
import org.learn.watchwave.interactions.repository.WatchLaterRepository;
import org.learn.watchwave.interactions.service.WatchLaterService;
import org.learn.watchwave.videos.service.VideoService;
import org.learn.watchwave.auth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WatchLaterServiceImpl implements WatchLaterService {

    private final WatchLaterRepository watchLaterRepository;
    private final UserRepository userRepository;
    private final VideoService videoService;

    @Override
    @Transactional
    public WatchLaterResponse addToWatchLater(UUID userId, WatchLaterRequest request) {
        if (!userRepository.existsById(userId)) throw new IllegalArgumentException("User not found");
        videoService.getVideoById(request.getVideoId());

        WatchLater entry = watchLaterRepository.findByUserIdAndVideoId(userId, request.getVideoId())
                .orElse(WatchLater.builder()
                        .userId(userId)
                        .videoId(request.getVideoId())
                        .addedAt(Instant.now())
                        .build());

        WatchLater saved = watchLaterRepository.save(entry);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void removeFromWatchLater(UUID userId, UUID videoId) {
        watchLaterRepository.deleteByUserIdAndVideoId(userId, videoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WatchLaterResponse> getWatchLaterList(UUID userId) {
        return watchLaterRepository.findByUserIdOrderByAddedAtDesc(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInWatchLater(UUID userId, UUID videoId) {
        return watchLaterRepository.findByUserIdAndVideoId(userId, videoId).isPresent();
    }

    private WatchLaterResponse toResponse(WatchLater entry) {
        WatchLaterResponse dto = new WatchLaterResponse();
        dto.setId(entry.getId());
        dto.setUserId(entry.getUserId());
        dto.setVideoId(entry.getVideoId());
        dto.setAddedAt(entry.getAddedAt());
        return dto;
    }
}
