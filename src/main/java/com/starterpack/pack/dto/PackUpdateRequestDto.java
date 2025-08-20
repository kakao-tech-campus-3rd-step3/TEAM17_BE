package com.starterpack.pack.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PackUpdateRequestDto(
        Long categoryId,
        @Size(min = 1, max = 100) String name,
        List<Long> productIds,
        @PositiveOrZero Integer totalCost,
        String description,
        String src
) {}
