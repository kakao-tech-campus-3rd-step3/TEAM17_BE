package com.starterpack.product.dto;

public record ProductUpdateRequestDto(
        String name,
        String link,
        String productType,
        String src,
        Integer cost,
        Long categoryId
) {
}
