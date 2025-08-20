package com.starterpack.pack.dto;

import com.starterpack.pack.entity.Pack;
import java.util.List;

public record PackResponseDto(
        Long id,
        String name,
        Integer cost,
        String description,
        List<List<String>> parts,
        Integer like
) {
    public static PackResponseDto from(Pack pack) {
        List<List<String>> parts = pack.getProducts().stream()
                .map(p -> List.of(p.getName(), p.getSrc()))
                .toList();

        return new PackResponseDto(
                pack.getId(),
                pack.getName(),
                pack.getTotalCost(),
                pack.getDescription(),
                parts,
                pack.getPackLikeCount()
        );
    }
}
