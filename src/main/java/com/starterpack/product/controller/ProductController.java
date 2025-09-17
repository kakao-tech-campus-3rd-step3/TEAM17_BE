package com.starterpack.product.controller;

import com.starterpack.product.dto.ProductCreateRequestDto;
import com.starterpack.product.dto.ProductDetailResponseDto;
import com.starterpack.product.dto.ProductSimpleResponseDto;
import com.starterpack.product.dto.ProductUpdateRequestDto;
import com.starterpack.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product", description = "상품 관리 API")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @Operation(summary = "상품 생성", description = "새로운 상품을 생성합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ProductDetailResponseDto> createProduct(
            @RequestBody ProductCreateRequestDto productCreateRequestDto
    ){
        ProductDetailResponseDto responseDto = productService.createProduct(productCreateRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping
    @Operation(summary = "상품 목록 조회", description = "모든 상품 목록을 조회합니다.")
    public ResponseEntity<List<ProductSimpleResponseDto>> getAllProducts() {
        List<ProductSimpleResponseDto> products = productService.getProducts();
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "상품 상세 조회", description = "특정 상품의 상세 정보를 조회합니다.")
    public ResponseEntity<ProductDetailResponseDto> getProductById(
            @PathVariable Long productId
    ){
        ProductDetailResponseDto responseDto = productService.getProductDetail(productId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "상품 수정", description = "기존 상품 정보를 수정합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ProductDetailResponseDto> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductUpdateRequestDto productUpdateRequestDto
    ){
        ProductDetailResponseDto responseDto = productService.updateProduct(productId, productUpdateRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long productId
    ){
        productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
