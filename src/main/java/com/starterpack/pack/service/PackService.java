package com.starterpack.pack.service;

import com.starterpack.category.entity.Category;
import com.starterpack.category.repository.CategoryRepository;
import com.starterpack.exception.BusinessException;
import com.starterpack.exception.ErrorCode;
import com.starterpack.member.entity.Member;
import com.starterpack.pack.dto.PackCreateRequestDto;
import com.starterpack.pack.dto.PackLikeResponseDto;
import com.starterpack.pack.dto.PackUpdateRequestDto;
import com.starterpack.pack.entity.Pack;
import com.starterpack.pack.entity.PackLike;
import com.starterpack.pack.repository.PackLikeRepository;
import com.starterpack.pack.repository.PackRepository;
import com.starterpack.product.entity.Product;
import com.starterpack.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
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
    private final PackLikeRepository packLikeRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<Pack> getPacks() {
        return packRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Pack> getPacksByCategory(Long categoryId) {
        return packRepository.findAllByCategoryIdWithProducts(categoryId);
    }

    @Transactional(readOnly = true)
    public Pack getPackDetail(Long id) {
        return packRepository.findWithProductsById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PACK_NOT_FOUND));
    }

    @Transactional
    public Pack create(PackCreateRequestDto req) {

        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        Set<Product> products = loadProducts(req.productIds());

        Pack pack = Pack.create(
                category,
                req.name(),
                req.description(),
                req.src(),
                products,
                req.totalCost()
        );

        return packRepository.save(pack);
    }

    @Transactional
    public Pack update(Long id, PackUpdateRequestDto req) {
        Pack pack = findPackById(id);

        validateUpdateRequest(req);

        Category newCategory = (req.categoryId() != null)
                ? findCategoryById(req.categoryId())
                : null;

        Set<Product> newProducts = (req.productIds() != null)
                ? loadProducts(req.productIds())
                : null;

        pack.applyUpdate(
                newCategory,
                req.name(),
                newProducts,
                req.totalCost(),
                req.description(),
                req.src()
        );
        return pack; 
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

    public PackLikeResponseDto togglePackLike(Long id, Member member) {
        Pack pack = findPackById(id);

        boolean exists = packLikeRepository.existsByPackAndMember(pack, member);

        if (exists) {
            packLikeRepository.deleteByPackAndMember(pack, member);
            packRepository.decrementLikeCount(id);
        } else {
            packLikeRepository.save(new PackLike(pack, member));
            packRepository.incrementLikeCount(id);
        }

        entityManager.refresh(pack);

        return PackLikeResponseDto.of(pack.getPackLikeCount(), !exists);
    }
}
