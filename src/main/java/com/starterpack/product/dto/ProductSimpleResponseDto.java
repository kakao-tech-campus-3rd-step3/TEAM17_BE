package com.starterpack.product.dto;

import com.starterpack.product.entity.Product;

public record ProductSimpleResponseDto(
        Long id,
        String name,
        String link,
        Integer cost
) {
    public static ProductSimpleResponseDto from(Product product) {
        return new ProductSimpleResponseDto(
                product.getId(),
                product.getName(),
                product.getLink(),
                product.getCost()
        );
    }
}
