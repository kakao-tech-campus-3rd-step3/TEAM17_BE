package com.starterpack.pack.dto;

import com.starterpack.pack.entity.PackItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record PackItemDto(
        @NotBlank(message = "아이템 이름은 필수입니다.")
        @Size(max = 200, message = "아이템 이름은 200자를 초과할 수 없습니다.")
        String name,

        @Size(max = 1000, message = "상품 링크는 1000자를 초과할 수 없습니다.")
        @URL(protocol = "http", regexp = "https?://.*", message = "유효한 URL 형식이 아닙니다.")
        String linkUrl,

        @Size(max = 1000, message = "설명은 1000자를 초과할 수 없습니다.")
        String description,

        @Size(max = 1000, message = "이미지 링크는 1000자를 초과할 수 없습니다.")
        @URL(protocol = "http", regexp = "https?://.*", message = "유효한 URL 형식이 아닙니다.")
        String imageUrl
) {
    public static PackItemDto from(PackItem item) {
        return new PackItemDto(
                item.getName(),
                item.getLinkUrl(),
                item.getDescription(),
                item.getImageUrl()
        );
    }
}
