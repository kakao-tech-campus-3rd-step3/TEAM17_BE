package com.starterpack.product.dto;

import com.starterpack.product.entity.Product;

public record ProductAdminListDto(
        Long id,
        String name,
        String link,
        String productType,
        Integer cost,
        String categoryName
) {
    public static ProductAdminListDto from(Product product) {
        String categoryName = product.getCategory().getName();

        return new ProductAdminListDto(
                product.getId(),
                product.getName(),
                product.getLink(),
                product.getProductType(),
                product.getCost(),
                categoryName
        );
    }
}
