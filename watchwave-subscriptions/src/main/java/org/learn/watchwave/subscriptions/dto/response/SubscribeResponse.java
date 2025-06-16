package org.learn.watchwave.subscriptions.dto.response;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;
@Data
public class SubscribeResponse {
    private UUID id;
    private UUID subscriberId;
    private UUID creatorId;
    private Instant subscribedAt;
}
