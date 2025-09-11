package com.starterpack.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record ProductCreateRequestDto(
        @NotBlank(message = "상품 이름은 필수입니다.")
        @Size(max = 200, message = "상품 이름은 200자를 초과할 수 없습니다.")
        String name,

        @URL(message = "유효한 URL 형식이 아닙니다.")
        String link,

        String productType,

        @URL(message = "유효한 이미지 URL 형식이 아닙니다.")
        String src,

        @NotNull(message = "가격을 입력해주세요.")
        @PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
        Integer cost,

        @NotNull(message = "카테고리를 선택해주세요.")
        Long categoryId
) {
    public static ProductCreateRequestDto emptyForm(){
        return new ProductCreateRequestDto(null, null, null, null, null, null);
    }
}