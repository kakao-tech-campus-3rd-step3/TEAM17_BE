package com.starterpack.pack.service;

import com.starterpack.category.entity.Category;
import com.starterpack.category.repository.CategoryRepository;
import com.starterpack.exception.BusinessException;
import com.starterpack.exception.ErrorCode;
import com.starterpack.pack.dto.*;
import com.starterpack.pack.entity.Pack;
import com.starterpack.pack.repository.PackRepository;
import com.starterpack.product.entity.Product;
import com.starterpack.product.repository.ProductRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public List<Pack> getPacks() {
        return packRepository.findAll();
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
                .orElseThrow(() -> new BusinessException(ErrorCode.PACK_NOT_FOUND));
        return PackDetailResponseDto.from(pack);
    }

    @Transactional
    public PackDetailResponseDto create(PackCreateRequestDto req) {

        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

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
        Pack pack = findPackById(id);
        
        validateUpdateRequest(req);
        updatePackFields(pack, req);
        
        return PackDetailResponseDto.from(pack);
    }

    // Pack 조회 메서드
    private Pack findPackById(Long id) {
        return packRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PACK_NOT_FOUND));
    }

    // 요청 데이터 검증
    private void validateUpdateRequest(PackUpdateRequestDto req) {
        if (req.name() != null && req.name().isBlank()) {
            throw new IllegalArgumentException("Pack name must not be blank");
        }
    }

    // Pack 필드 업데이트
    private void updatePackFields(Pack pack, PackUpdateRequestDto req) {
        updateBasicFields(pack, req);
        updateProductsAndCost(pack, req);
    }

    // 기본 필드 업데이트
    private void updateBasicFields(Pack pack, PackUpdateRequestDto req) {
        if (req.name() != null) {
            pack.setName(req.name());
        }
        
        if (req.categoryId() != null) {
            Category category = findCategoryById(req.categoryId());
            pack.setCategory(category);
        }
        
        if (req.description() != null) {
            pack.setDescription(req.description());
        }
        
        if (req.src() != null) {
            pack.setSrc(req.src());
        }
    }

    // 제품 및 비용 업데이트
    private void updateProductsAndCost(Pack pack, PackUpdateRequestDto req) {
        if (req.productIds() != null) {
            updateProducts(pack, req.productIds());
            updateTotalCost(pack, req.totalCost());
        } else if (req.totalCost() != null) {
            pack.setTotalCost(req.totalCost());
        }
    }

    // 제품 업데이트
    private void updateProducts(Pack pack, List<Long> productIds) {
        // 기존 제품 제거
        for (Product product : new HashSet<>(pack.getProducts())) {
            pack.removeProduct(product);
        }
        
        // 새 제품 추가
        Set<Product> products = loadProducts(productIds);
        for (Product product : products) {
            pack.addProduct(product);
        }
    }

    // 총 비용 업데이트
    private void updateTotalCost(Pack pack, Integer requestedTotalCost) {
        int totalCost = (requestedTotalCost != null) 
            ? requestedTotalCost 
            : calcTotalCost(pack.getProducts());
        pack.setTotalCost(totalCost);
    }

    // 카테고리 조회 메서드
    private Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    @Transactional
    public void delete(Long id) {
        Pack pack = packRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PACK_NOT_FOUND));
        for (Product p : new HashSet<>(pack.getProducts())) {
            pack.removeProduct(p);
        }
        packRepository.delete(pack);
    }


    private Set<Product> loadProducts(List<Long> productIds) {
        List<Product> found = productRepository.findAllById(productIds);
        if (found.size() != productIds.size()) {
            Set<Long> foundIds = found.stream()
                    .map(Product::getId)
                    .collect(Collectors.toSet());

            List<Long> missingIds = productIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            String detailMessage = String.format("다음 ID에 해당하는 상품을 찾을 수 없습니다: %s", missingIds);

            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, detailMessage);
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
