package com.starterpack.product.dto;

import com.starterpack.product.entity.Product;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductRequestDto {
    private Long id;

    @NotEmpty(message = "상품명은 필수 입력 값입니다.")
    private String name;

    private String link;
    private String productType;
    private String src;

    @NotNull(message = "가격은 필수 입력 값입니다.")
    @PositiveOrZero(message = "가격은 0 이상의 값이어야 합니다.")
    private Integer cost;

    private Long categoryId;

    public ProductRequestDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.link = product.getLink();
        this.productType = product.getProductType();
        this.src = product.getSrc();
        this.cost = product.getCost();
        if (product.getCategory() != null) {
            this.categoryId = product.getCategory().getId();
        }
    }
}
