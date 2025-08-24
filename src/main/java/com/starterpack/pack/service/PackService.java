package com.starterpack.pack.service;

import com.starterpack.category.entity.Category;
import com.starterpack.category.repository.CategoryRepository;
import com.starterpack.pack.dto.*;
import com.starterpack.pack.entity.Pack;
import com.starterpack.pack.repository.PackRepository;
import com.starterpack.product.entity.Product;
import com.starterpack.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PackService {

    private final PackRepository packRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<PackResponseDto> getPacks() {
        return packRepository.findAll().stream()
                .map(PackResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PackResponseDto> getPacksByCategory(Long categoryId) {
        return packRepository.findAllByCategoryIdWithProducts(categoryId).stream()
                .map(PackResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PackDetailResponseDto getPackDetail(Long id) {
        Pack pack = packRepository.findWithProductsById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pack not found: " + id));
        return PackDetailResponseDto.from(pack);
    }

    @Transactional
    public PackDetailResponseDto create(PackCreateRequestDto req) {

        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + req.categoryId()));

        Set<Product> products = loadProducts(req.productIds());

        Pack pack = new Pack();
        pack.setCategory(category);
        if (req.name() == null || req.name().isBlank()) {
            throw new IllegalArgumentException("Pack name must not be blank");
        }
        pack.setName(req.name());
        pack.setDescription(req.description());
        pack.setSrc(req.src());
        pack.setPackLikeCount(0);

        int totalCost = (req.totalCost() != null) ? req.totalCost() : calcTotalCost(products);
        pack.setTotalCost(totalCost);

        for (Product p : products) {
            pack.addProduct(p);
        }

        Pack saved = packRepository.save(pack);
        return PackDetailResponseDto.from(saved);
    }

    @Transactional
    public PackDetailResponseDto update(Long id, PackUpdateRequestDto req) {
        Pack pack = packRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pack not found: " + id));


        if (req.name() != null) {
            if (req.name().isBlank()) throw new IllegalArgumentException("Pack name must not be blank");
            pack.setName(req.name());
        }


        if (req.categoryId() != null) {
            Category category = categoryRepository.findById(req.categoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found: " + req.categoryId()));
            pack.setCategory(category);
        }


        if (req.description() != null) pack.setDescription(req.description());
        if (req.src() != null) pack.setSrc(req.src());

        // products 변경 시 연관관계 재설정 + totalCost 재계산
        if (req.productIds() != null) {
            for (Product p : new HashSet<>(pack.getProducts())) {
                pack.removeProduct(p);
            }
            Set<Product> products = loadProducts(req.productIds());
            for (Product p : products) {
                pack.addProduct(p);
            }
            int total = (req.totalCost() != null) ? req.totalCost() : calcTotalCost(pack.getProducts());
            pack.setTotalCost(total);
        } else if (req.totalCost() != null) {
            // 제품 미변경 + 금액만 변경
            pack.setTotalCost(req.totalCost());
        }

        return PackDetailResponseDto.from(pack);
    }

    @Transactional
    public void delete(Long id) {
        Pack pack = packRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pack not found: " + id));
        for (Product p : new HashSet<>(pack.getProducts())) {
            pack.removeProduct(p);
        }
        packRepository.delete(pack);
    }


    private Set<Product> loadProducts(List<Long> productIds) {
        List<Product> found = productRepository.findAllById(productIds);
        if (found.size() != productIds.size()) {
            throw new EntityNotFoundException("Some products not found: " + productIds);
        }
        return new HashSet<>(found);
    }

    private int calcTotalCost(Set<Product> products) {
        int sum = 0;
        for (Product p : products) {
            if (p.getCost() != null) {
                sum += p.getCost();
            }
        }
        return sum;
    }
}
