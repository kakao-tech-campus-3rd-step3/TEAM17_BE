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

@Service
@Transactional
public class ProductService {

    private static final Map<String, Comparator<ProductAdminListDto>> COMPARATORS;

    static {
        COMPARATORS = new HashMap<>();
        COMPARATORS.put("name", Comparator.comparing(ProductAdminListDto::name));
        COMPARATORS.put("cost", Comparator.comparing(ProductAdminListDto::cost));
        COMPARATORS.put("category", Comparator.comparing(ProductAdminListDto::categoryName));
        COMPARATORS.put("id", Comparator.comparing(ProductAdminListDto::id));
    }

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
            CategoryRepository categoryRepository) {
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

    // 페이지네이션을 지원하는 메서드들
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
    public Page<ProductAdminListDto> searchProductsForAdminWithCategoryAndPagination(String keyword,
            Long categoryId, Pageable pageable) {
        Page<Product> productPage;
        if (categoryId != null) {
            productPage = productRepository.findByNameContainingIgnoreCaseAndCategoryId(keyword,
                    categoryId, pageable);
        } else {
            productPage = productRepository.findByNameContainingIgnoreCase(keyword, pageable);
        }
        return productPage.map(ProductAdminListDto::from);
    }

    // 상품명으로 검색하는 메서드
    @Transactional(readOnly = true)
    public List<ProductAdminListDto> searchProductsForAdmin(String keyword) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword);
        return products.stream()
                .map(ProductAdminListDto::from)
                .collect(Collectors.toList());
    }

    // 카테고리별 필터링하는 메서드
    @Transactional(readOnly = true)
    public List<ProductAdminListDto> getProductsForAdminByCategory(Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        return products.stream()
                .map(ProductAdminListDto::from)
                .collect(Collectors.toList());
    }

    // 검색과 카테고리 필터링을 함께 하는 메서드
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

    // 상품 목록을 정렬하는 메서드
    public List<ProductAdminListDto> sortProducts(List<ProductAdminListDto> products,
                                                  String sortBy,
                                                  String sortOrder) {
        Comparator<ProductAdminListDto> comparator =
                COMPARATORS.getOrDefault(sortBy, COMPARATORS.get("id"));

        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }

        return products.stream()
                .sorted(comparator)
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
}
