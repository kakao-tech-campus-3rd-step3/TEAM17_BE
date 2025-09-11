package com.starterpack.feed.dto;

import com.starterpack.feed.entity.FeedType;
import com.starterpack.product.dto.ProductCreateRequestDto;
import java.util.List;

public record FeedCreateRequestDto(
    String description,
    String imageUrl,
    FeedType feedType,
    Long categoryId,
    List<ProductTagRequestDto> products
) {
}
