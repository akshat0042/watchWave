package org.learn.watchwave.subscriptions.services.impl;

import org.learn.watchwave.auth.model.entity.User;
import org.learn.watchwave.subscriptions.dto.request.SubscribeRequest;
import org.learn.watchwave.subscriptions.dto.response.SubscribeResponse;
import org.learn.watchwave.subscriptions.model.Subscribe;
import org.learn.watchwave.subscriptions.services.SubscribeService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.learn.watchwave.auth.repository.UserRepository;
import org.learn.watchwave.subscriptions.repository.SubscribeRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscribeServiceImpl implements SubscribeService {
    private final SubscribeRepository subscribeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SubscribeResponse subscribe(UUID subscriberId, SubscribeRequest request){
        if(!userRepository.existsById(subscriberId)){
            throw new IllegalArgumentException("Subscriber not found");
        }
        // Fetch the creator user entity
        User creator = userRepository.findById(request.getCreatorId())
                .orElseThrow(() -> new IllegalArgumentException("Creator not found"));

        if (subscriberId.equals(request.getCreatorId())) {
            throw new IllegalArgumentException("Cannot subscribe to yourself");
        }

        // Check if the creator has the CREATOR role
        boolean isCreator = creator.getRoles().stream()
                .anyMatch(userRole -> "CREATOR".equalsIgnoreCase(userRole.getRole().getRoleName()));

        if (!isCreator) {
            throw new IllegalArgumentException("You can only subscribe to users with the CREATOR role.");
        }

        Subscribe subscribe = subscribeRepository.findBySubscriberIdAndCreatorId(subscriberId, request.getCreatorId())
                .orElse(Subscribe.builder()
                        .subscriberId(subscriberId)
                        .creatorId(request.getCreatorId())
                        .subscribedAt(Instant.now())
                        .build());

        Subscribe saved = subscribeRepository.save(subscribe);
        return toResponse(saved);
    }


    @Override
    @Transactional
    public void unsubscribe(UUID subscriberId, UUID creatorId) {
        subscribeRepository.deleteBySubscriberIdAndCreatorId(subscriberId, creatorId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscribeResponse> getSubscriptions(UUID subscriberId) {
        return subscribeRepository.findBySubscriberIdOrderBySubscribedAtDesc(subscriberId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<SubscribeResponse> getSubscribers(UUID creatorId) {
        return subscribeRepository.findByCreatorIdOrderBySubscribedAtDesc(creatorId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public boolean isSubscribed(UUID subscriberId, UUID creatorId) {
        return subscribeRepository.existsBySubscriberIdAndCreatorId(subscriberId, creatorId);
    }

    private SubscribeResponse toResponse(Subscribe sub) {
        SubscribeResponse dto = new SubscribeResponse();
        dto.setId(sub.getId());
        dto.setSubscriberId(sub.getSubscriberId());
        dto.setCreatorId(sub.getCreatorId());
        dto.setSubscribedAt(sub.getSubscribedAt());
        return dto;
    }
}
