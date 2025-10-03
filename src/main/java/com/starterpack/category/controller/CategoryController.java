package com.starterpack.category.controller;

import com.starterpack.category.dto.CategoryResponseDto;
import com.starterpack.category.entity.Category;
import com.starterpack.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "카테고리", description = "카테고리 관련 API")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "전체 카테고리 조회", description = "모든 카테고리 목록을 조회합니다.")
    public ResponseEntity<List<CategoryResponseDto>> findAllCategories() {
        List<Category> categories = categoryService.findAllCategories();
        List<CategoryResponseDto> responseDtos = categories.stream()
                .map(CategoryResponseDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtos);
    }

}
