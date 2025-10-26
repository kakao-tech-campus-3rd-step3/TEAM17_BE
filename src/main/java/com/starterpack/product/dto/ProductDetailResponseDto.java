package com.starterpack.product.dto;

import com.starterpack.pack.entity.Pack;
import com.starterpack.product.entity.Product;

import java.util.List;

public record ProductDetailResponseDto(
        Long id,
        String name,
        String link,
        String productType,
        String src,
        Integer cost,
        Integer likeCount,
        Long categoryId,
        String categoryName
) {
    public static ProductDetailResponseDto from(Product product) {
        Long categoryId = (product.getCategory() != null) ? product.getCategory().getId() : null;
        String categoryName = (product.getCategory() != null) ? product.getCategory().getName() : "미지정";
        return new ProductDetailResponseDto(
                product.getId(),
                product.getName(),
                product.getLink(),
                product.getProductType(),
                product.getSrc(),
                product.getCost(),
                product.getLikeCount(),
                categoryId,
                categoryName
        );
    }
}
