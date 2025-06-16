package org.learn.watchwave.subscriptions.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class SubscribeRequest {
    private UUID creatorId;
}
