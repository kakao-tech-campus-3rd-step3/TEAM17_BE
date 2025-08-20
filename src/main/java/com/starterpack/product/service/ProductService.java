package com.starterpack.product.service;

import com.starterpack.category.entity.Category;
import com.starterpack.category.repository.CategoryRepository;

import com.starterpack.product.dto.ProductCreateRequestDto;
import com.starterpack.product.dto.ProductDetailResponseDto;
import com.starterpack.product.dto.ProductSimpleResponseDto;
import com.starterpack.product.dto.ProductUpdateRequestDto;
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

    public Product saveProduct(Product product, Long categoryId) {
        if (product.getId() != null) {
            Product existingProduct = findProductById(product.getId());
            existingProduct.setName(product.getName());
            existingProduct.setLink(product.getLink());
            existingProduct.setProductType(product.getProductType());
            existingProduct.setSrc(product.getSrc());
            existingProduct.setCost(product.getCost());
            product = existingProduct;
        }

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + categoryId));
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }
        return productRepository.save(product);
    }


    public Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    //여기서부터 시작
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

        return ProductDetailResponseDto.from(productRepository.save(product));
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
