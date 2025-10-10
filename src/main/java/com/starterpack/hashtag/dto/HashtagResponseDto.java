package com.starterpack.hashtag.dto;

import com.starterpack.hashtag.entity.Hashtag;

public record HashtagResponseDto(Long id, String hashtagName) {
    public static HashtagResponseDto from(Hashtag hashtag) {
        return new HashtagResponseDto(hashtag.getId(), hashtag.getName());
    }
}
