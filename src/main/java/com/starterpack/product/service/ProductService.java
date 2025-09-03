package com.starterpack.product.service;

import com.starterpack.category.entity.Category;
import com.starterpack.category.repository.CategoryRepository;

import com.starterpack.product.dto.*;
import com.starterpack.product.entity.Product;
import com.starterpack.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ProductDetailResponseDto createProduct(ProductCreateRequestDto productCreateRequestDto) {
        Category category = getCategoryByCategoryId(productCreateRequestDto.categoryId());

        Product product = new Product(
                productCreateRequestDto.name(),
                productCreateRequestDto.link(),
                productCreateRequestDto.productType(),
                productCreateRequestDto.src(),
                productCreateRequestDto.cost(),
                category
        );

        return ProductDetailResponseDto.from(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public ProductDetailResponseDto getProductDetail(Long productId) {
        Product product = productRepository.findById(productId).
                orElseThrow(() -> new IllegalArgumentException(productId + "에 해당하는 상품을 찾지 못했습니다."));

        return ProductDetailResponseDto.from(product);
    }

    @Transactional(readOnly = true)
    public List<ProductSimpleResponseDto> getProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(ProductSimpleResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductAdminListDto> getProductsForAdmin(){
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(ProductAdminListDto::from)
                .collect(Collectors.toList());
    }

    // 상품명으로 검색하는 메서드
    @Transactional(readOnly = true)
    public List<ProductAdminListDto> searchProductsForAdmin(String keyword) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword);
        return products.stream()
                .map(ProductAdminListDto::from)
                .collect(Collectors.toList());
    }

    public ProductDetailResponseDto updateProduct(Long productId, ProductUpdateRequestDto productUpdateRequestDto) {
        Product product = getProduct(productId);

        Category category = getCategoryByCategoryId(productUpdateRequestDto.categoryId());

        product.update(
                productUpdateRequestDto.name(),
                productUpdateRequestDto.link(),
                productUpdateRequestDto.productType(),
                productUpdateRequestDto.src(),
                productUpdateRequestDto.cost(),
                category);

        return ProductDetailResponseDto.from(product);
    }

    public void deleteProduct(Long productId) {
        if(!productRepository.existsById(productId)) {
            throw new IllegalArgumentException(productId + "번에 해당하는 상품을 찾을 수 없습니다.");
        }

        productRepository.deleteById(productId);
    }

    private Category getCategoryByCategoryId(Long categoryId) {
        return categoryRepository.findById(categoryId).
                orElseThrow(() -> new IllegalArgumentException(categoryId + "번에 해당하는 카테고리를 찾지 못했습니다."));
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(productId + "번에 해당하는 상품을 찾지 못했습니다."));
    }
}
