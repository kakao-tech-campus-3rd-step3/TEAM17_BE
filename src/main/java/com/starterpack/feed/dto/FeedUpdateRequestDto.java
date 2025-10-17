package com.starterpack.feed.dto;

import com.starterpack.hashtag.validator.HashtagCheck;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record FeedUpdateRequestDto(
        @NotBlank(message = "피드 설명은 필수입니다.")
        @Size(max = 2000, message = "피드 설명은 2000자까지 입니다.")
        String description,

        @NotBlank(message = "이미지 링크는 필수입니다.")
        @Size(max = 500, message= "이미지 링크는 500자까지 입니다.")
        String imageUrl,

        @NotNull(message = "취미 카테고리는 필수입니다.")
        Long categoryId,

        @Size(max = 10, message = "해쉬태그는 최대 10개까지 가능합니다.")
        @HashtagCheck
        List<String> hashtagNames
) {
}
