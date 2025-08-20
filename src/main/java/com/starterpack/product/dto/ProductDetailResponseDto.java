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
        List<PackInfo> packs
) {
    public static ProductDetailResponseDto from(Product product) {
        List<PackInfo> packInfos = product.getPacks().stream()
                .map(PackInfo::from)
                .toList();

        return new ProductDetailResponseDto(
                product.getId(),
                product.getName(),
                product.getLink(),
                product.getProductType(),
                product.getSrc(),
                product.getCost(),
                product.getLikeCount(),
                product.getCategory().getId(),
                packInfos
        );
    }

    public record PackInfo(
            Long id,
            String name
    ) {
        public static PackInfo from(Pack pack) {
            return new PackInfo(pack.getId(), pack.getDescription());
        }
    }
}
