package com.starterpack.category.dto;

import com.starterpack.category.entity.Category;
import lombok.Getter;

@Getter
public class CategoryResponseDto {
    private Long id;
    private String name;

    public CategoryResponseDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }
}
