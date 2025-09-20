package com.starterpack.product.service;

import com.starterpack.category.entity.Category;
import com.starterpack.category.repository.CategoryRepository;

import com.starterpack.exception.BusinessException;
import com.starterpack.exception.ErrorCode;
import com.starterpack.product.dto.*;
import com.starterpack.product.entity.Product;
import com.starterpack.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ProductDetailResponseDto createProduct(ProductCreateRequestDto productCreateRequestDto) {
        Category category = getCategoryByCategoryId(productCreateRequestDto.categoryId());

        Product product = Product.create(
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
                orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

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
    public List<ProductAdminListDto> getProductsForAdmin() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(ProductAdminListDto::from)
                .collect(Collectors.toList());
    }

    // 관리자 목록 DB정렬로 가져오도록
    @Transactional(readOnly = true)
    public List<ProductAdminListDto> getProductsForAdminSorted(String sortBy, String sortOrder) {
        Sort sort = buildSort(sortBy, sortOrder);
        return productRepository.findAll(sort).stream()
                .map(ProductAdminListDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ProductAdminListDto> getProductsForAdminWithPagination(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(ProductAdminListDto::from);
    }

    @Transactional(readOnly = true)
    public Page<ProductAdminListDto> searchProductsForAdminWithPagination(String keyword,
            Pageable pageable) {
        Page<Product> productPage = productRepository.findByNameContainingIgnoreCase(keyword,
                pageable);
        return productPage.map(ProductAdminListDto::from);
    }

    @Transactional(readOnly = true)
    public Page<ProductAdminListDto> getProductsForAdminByCategoryWithPagination(Long categoryId,
            Pageable pageable) {
        Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageable);
        return productPage.map(ProductAdminListDto::from);
    }

    @Transactional(readOnly = true)
    public Page<ProductAdminListDto> searchProductsForAdmin(String keyword, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable)
                .map(ProductAdminListDto::from);
    }

    @Transactional(readOnly = true)
    public Page<ProductAdminListDto> searchProductsForAdminWithCategory(
            String keyword, Long categoryId, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCaseAndCategoryId(keyword, categoryId, pageable)
                .map(ProductAdminListDto::from);
    }

    @Transactional(readOnly = true)
    public List<ProductAdminListDto> searchProductsForAdmin(String keyword) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword);
        return products.stream()
                .map(ProductAdminListDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductAdminListDto> getProductsForAdminByCategory(Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        return products.stream()
                .map(ProductAdminListDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductAdminListDto> searchProductsForAdminWithCategory(String keyword,
            Long categoryId) {
        List<Product> products;
        if (categoryId != null) {
            products = productRepository.findByNameContainingIgnoreCaseAndCategoryId(keyword,
                    categoryId);
        } else {
            products = productRepository.findByNameContainingIgnoreCase(keyword);
        }
        return products.stream()
                .map(ProductAdminListDto::from)
                .collect(Collectors.toList());
    }

    public ProductDetailResponseDto updateProduct(Long productId,
            ProductUpdateRequestDto productUpdateRequestDto) {
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
        if (!productRepository.existsById(productId)) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        productRepository.deleteById(productId);
    }

    private Category getCategoryByCategoryId(Long categoryId) {
        return categoryRepository.findById(categoryId).
                orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    // 정렬을 위한 추가 메소드
    private Sort buildSort(String sortBy, String sortOrder) {
        String property = (sortBy == null || sortBy.isBlank()) ? "id" : mapSortProperty(sortBy);
        Sort.Direction direction = Sort.Direction.fromString(sortOrder == null ? "asc" : sortOrder);
        return Sort.by(direction, property);
    }

    private String mapSortProperty(String sortBy) {
        return switch (sortBy) {
            case "category", "categoryName" -> "category.name"; // 카테고리명으로 정렬
            case "name", "cost", "id" -> sortBy;
            default -> "id";
        };
    }
}
