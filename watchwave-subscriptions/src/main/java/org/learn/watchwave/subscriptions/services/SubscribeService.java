package org.learn.watchwave.subscriptions.services;

import org.learn.watchwave.subscriptions.dto.request.SubscribeRequest;
import org.learn.watchwave.subscriptions.dto.response.SubscribeResponse;

import java.util.List;
import java.util.UUID;

public interface SubscribeService {
    SubscribeResponse subscribe(UUID subscriberId, SubscribeRequest request);
    void unsubscribe(UUID subscriberId, UUID creatorId);
    List<SubscribeResponse> getSubscriptions(UUID subscriberId);
    List<SubscribeResponse> getSubscribers(UUID creatorId);
    boolean isSubscribed(UUID subscriberId, UUID creatorId);
}