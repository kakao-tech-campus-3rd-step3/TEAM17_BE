package com.starterpack.product.controller;

import com.starterpack.product.dto.ProductCreateRequestDto;
import com.starterpack.product.dto.ProductDetailResponseDto;
import com.starterpack.product.dto.ProductSimpleResponseDto;
import com.starterpack.product.dto.ProductUpdateRequestDto;
import com.starterpack.product.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductDetailResponseDto> createProduct(
            @RequestBody ProductCreateRequestDto productCreateRequestDto
    ){
        ProductDetailResponseDto responseDto = productService.createProduct(productCreateRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<ProductSimpleResponseDto>> getAllProducts() {
        List<ProductSimpleResponseDto> products = productService.getProducts();
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponseDto> getProductById(
            @PathVariable Long productId
    ){
        ProductDetailResponseDto responseDto = productService.getProductDetail(productId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDetailResponseDto> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductUpdateRequestDto productUpdateRequestDto
    ){
        ProductDetailResponseDto responseDto = productService.updateProduct(productId, productUpdateRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long productId
    ){
        productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
