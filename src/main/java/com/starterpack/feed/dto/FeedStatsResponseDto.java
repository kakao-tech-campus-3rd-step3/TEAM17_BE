package com.starterpack.feed.dto;

import com.starterpack.feed.entity.Feed;

public record FeedStatsResponseDto(
        Long likeCount,
        Long bookmarkCount,
        Long commentCount
) {
    public static FeedStatsResponseDto from(Feed feed) {
        return new FeedStatsResponseDto(
                feed.getLikeCount(),
                feed.getBookmarkCount(),
                feed.getCommentCount()
        );
    }
}
