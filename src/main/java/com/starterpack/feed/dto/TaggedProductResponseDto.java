package com.starterpack.feed.dto;

import com.starterpack.feed.entity.Feed;
import com.starterpack.feed.entity.FeedProduct;
import com.starterpack.product.entity.Product;

public record TaggedProductResponseDto(
        Long productId,
        String name,
        String imageUrl,
        String description
) {
    public static TaggedProductResponseDto from(FeedProduct feedProduct) {
        Product product = feedProduct.getProduct();
        return new TaggedProductResponseDto(
                product.getId(),
                product.getName(),
                product.getSrc(),
                feedProduct.getDescription()
        );
    }
}
