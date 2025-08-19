package com.starterpack.product.service;

import com.starterpack.category.entity.Category;
import com.starterpack.category.repository.CategoryRepository;
import com.starterpack.product.entity.Product;
import com.starterpack.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
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

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }
}
