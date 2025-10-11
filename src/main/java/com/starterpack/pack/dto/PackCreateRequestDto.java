package com.starterpack.pack.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PackCreateRequestDto(
        @NotNull(message = "카테고리는 필수입니다.")
        Long categoryId,

        @NotBlank(message = "팩 이름은 필수입니다.")
        @Size(max = 100, message = "팩 이름은 100자를 초과할 수 없습니다.")
        String name,

        @PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
        Integer price,

        @Size(max = 1000, message = "이미지 링크는 1000자를 초과할 수 없습니다.")
        String mainImageUrl,

        @Size(max = 5000, message = "설명은 5000자를 초과할 수 없습니다.")
        String description,

        @NotEmpty(message = "최소 1개 이상의 아이템이 필요합니다.")
        @Valid
        List<PackItemDto> items
) {
    public static final PackCreateRequestDto EMPTY_FORM = new PackCreateRequestDto(
            null,
            "",
            null,
            "",
            "",
            List.of()
    );
}
