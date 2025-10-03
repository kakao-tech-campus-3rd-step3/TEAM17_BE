package com.starterpack.category.dto;

import com.starterpack.category.entity.Category;
import lombok.Getter;

@Getter
public class CategoryResponseDto {
    private Long id;
    private String name;
    private String src;

    public CategoryResponseDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.src = category.getSrc();
    }
}
