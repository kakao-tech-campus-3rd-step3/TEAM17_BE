package com.starterpack.pack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PackCreateRequestDto(
        @NotNull Long categoryId,
        @NotBlank @Size(max = 100) String name,
        @NotEmpty List<@NotNull Long> productIds,
        @PositiveOrZero Integer totalCost,
        String description,
        String src
) {
    public static PackCreateRequestDto emptyForm() {
        return new PackCreateRequestDto(null, "", null, null, "","");
    }
}
