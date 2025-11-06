package com.starterpack.member.dto;

import com.starterpack.feed.dto.FeedResponseDto;
import com.starterpack.member.entity.Member;
import com.starterpack.pack.dto.PackDetailResponseDto;
import com.starterpack.pack.entity.Pack;
import com.starterpack.feed.entity.Feed;
import java.util.List;

// 마이페이지 응답 DTO
public record MyPageResponseDto(
        String nickname,
        String hobby,
        String profileImageUrl,
        String bio,
        long totalPostCount,
        long packCount,
        long feedCount,
        List<MyPagePackDto> packs,
        List<MyPageFeedDto> feeds,
        List<FeedResponseDto> bookmarkedFeeds,
        List<PackDetailResponseDto> bookmarkedPacks,
        boolean isMe
) {
    public static MyPageResponseDto from(Member member, List<Pack> packs, List<Feed> feeds, boolean isMe, List<FeedResponseDto> bookmarkedFeeds, List<PackDetailResponseDto> bookmarkedPacks) {
        List<MyPagePackDto> packDtos = packs.stream()
                .map(MyPagePackDto::from)
                .toList();
        
        List<MyPageFeedDto> feedDtos = feeds.stream()
                .map(MyPageFeedDto::from)
                .toList();
        
        long totalPostCount = packs.size() + feeds.size();
        
        return new MyPageResponseDto(
                member.getNickname(),
                member.getHobby(),
                member.getProfileImageUrl(),
                member.getBio(),
                totalPostCount,
                packs.size(),
                feeds.size(),
                packDtos,
                feedDtos,
                bookmarkedFeeds != null ? bookmarkedFeeds : List.of(),
                bookmarkedPacks != null ? bookmarkedPacks : List.of(),
                isMe
        );
    }
}

