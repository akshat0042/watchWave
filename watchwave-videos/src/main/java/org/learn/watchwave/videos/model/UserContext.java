package org.learn.watchwave.videos.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContext {
    private UUID userId;
    private String username;
    private String primaryRole;
    private List<String> roles;
}
