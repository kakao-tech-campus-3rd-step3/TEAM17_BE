package com.starterpack.pack.dto;

import com.starterpack.pack.entity.Pack;
import java.util.List;
import java.util.Objects;

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
                .map(p -> List.of(
                        Objects.toString(p.getName(), ""),
                        Objects.toString(p.getSrc(), "")
                ))
                .toList();

        return new PackResponseDto(
                pack.getId(),
                Objects.toString(pack.getName(), ""),
                pack.getTotalCost(),
                Objects.toString(pack.getDescription(), ""),
                parts,
                pack.getPackLikeCount()
        );
    }
}
