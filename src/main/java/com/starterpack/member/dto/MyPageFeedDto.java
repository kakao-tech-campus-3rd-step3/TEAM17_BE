package com.starterpack.member.dto;

import com.starterpack.feed.entity.Feed;

// 마이페이지에서 사용할 간단한 피드 정보
public record MyPageFeedDto(
        Long feedId,
        String description,
        String imageUrl
) {
    public static MyPageFeedDto from(Feed feed) {
        return new MyPageFeedDto(
                feed.getId(),
                feed.getDescription(),
                feed.getImageUrl()
        );
    }
}

