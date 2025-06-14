package org.learn.watchwave.interactions.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoLikeCount {
    private long likes;
    private long dislikes;
}
