package com.starterpack.pack.dto;

import com.starterpack.hashtag.validator.HashtagCheck;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PackUpdateRequestDto(
        Long categoryId,

        @Size(max = 100, message = "팩 이름은 100자를 초과할 수 없습니다.")
        String name,

        @PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
        Integer price,

        @Size(max = 1000, message = "이미지 링크는 1000자를 초과할 수 없습니다.")
        String mainImageUrl,

        @Size(max = 5000, message = "설명은 5000자를 초과할 수 없습니다.")
        String description,

        @Valid
        List<PackItemDto> items,

        @Size(max = 10, message = "해쉬태그는 최대 10개까지 가능합니다.")
        @HashtagCheck
        List<String> hashtagNames
) {
}
