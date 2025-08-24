package com.starterpack.pack.dto;

import com.starterpack.pack.entity.Pack;
import com.starterpack.product.entity.Product;
import java.util.List;

public record PackDetailResponseDto(
        Long id,
        String name,
        Integer cost,
        String description,
        String src,
        Integer like,
        List<PartDto> parts
) {
    public static PackDetailResponseDto from(Pack pack) {
        List<PartDto> parts = pack.getProducts().stream()
                .map(PartDto::from)
                .toList();

        return new PackDetailResponseDto(
                pack.getId(),
                pack.getName(),
                pack.getTotalCost(),
                pack.getDescription(),
                pack.getSrc(),
                pack.getPackLikeCount(),
                parts
        );
    }

    public record PartDto(Long productId, String name, String imageUrl, Integer price) {
        public static PartDto from(Product p) {
            return new PartDto(p.getId(), p.getName(), p.getSrc(), p.getCost());
        }
    }
}
