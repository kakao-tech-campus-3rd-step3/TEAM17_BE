package com.starterpack.pack.service;

import com.starterpack.category.entity.Category;
import com.starterpack.category.repository.CategoryRepository;
import com.starterpack.exception.BusinessException;
import com.starterpack.exception.ErrorCode;
import com.starterpack.hashtag.dto.HashtagUpdateResult;
import com.starterpack.hashtag.entity.Hashtag;
import com.starterpack.hashtag.service.HashtagService;
import com.starterpack.member.entity.Member;
import com.starterpack.member.entity.Role;
import com.starterpack.pack.dto.InteractionStatusResponseDto;
import com.starterpack.pack.dto.PackBookmarkResponseDto;
import com.starterpack.pack.dto.PackCreateRequestDto;
import com.starterpack.pack.dto.PackDetailResponseDto;
import com.starterpack.pack.dto.PackItemDto;
import com.starterpack.pack.dto.PackLikeResponseDto;
import com.starterpack.pack.dto.PackUpdateRequestDto;
import com.starterpack.pack.entity.Pack;
import com.starterpack.pack.entity.PackItem;
import com.starterpack.pack.entity.PackLike;
import com.starterpack.pack.repository.PackLikeRepository;
import com.starterpack.pack.entity.PackBookmark;
import com.starterpack.pack.repository.PackBookmarkRepository;
import com.starterpack.pack.repository.PackRepository;
import com.starterpack.product.entity.Product;
import com.starterpack.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PackService {

    private final PackRepository packRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PackLikeRepository packLikeRepository;
    private final PackBookmarkRepository packBookmarkRepository;
    private final EntityManager entityManager;
    private final HashtagService hashtagService;

    @Transactional(readOnly = true)
    public List<PackDetailResponseDto> getPacks(Member member) {
        List<Pack> packs = packRepository.findAll();

        return convertPacksToDto(packs, member);
    }

    @Transactional(readOnly = true)
    public List<Pack> getPacksForAdmin() {
        List<Pack> packs = packRepository.findAll();
        packs.forEach(pack ->
                pack.getPackHashtags().forEach(ph -> ph.getHashtag().getName())
        );
        return packs;

    }

    @Transactional(readOnly = true)
    public List<PackDetailResponseDto> getPacksByCategory(Long categoryId, Member member) {
        List<Pack> packs = packRepository.findAllByCategoryIdWithItems(categoryId);

        return convertPacksToDto(packs, member);
    }

    @Transactional(readOnly = true)
    public List<Pack> getPacksByCategoryForAdmin(Long categoryId) {
        List<Pack> packs = packRepository.findAllByCategoryIdWithItems(categoryId);
        packs.forEach(pack ->
                pack.getPackHashtags().forEach(ph -> ph.getHashtag().getName())
        );
        return packs;
    }

    @Transactional(readOnly = true)
    public PackDetailResponseDto getPackDetail(Long id, Member member) {
        Pack pack = packRepository.findWithItemsById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PACK_NOT_FOUND));

        if (member == null) {
            return PackDetailResponseDto.forAnonymous(pack);
        } else {
            boolean isLiked = packLikeRepository.existsByPackAndMember(pack, member);
            boolean isBookmarked = packBookmarkRepository.existsByPackAndMember(pack, member);

            return PackDetailResponseDto.forMember(
                    pack,
                    InteractionStatusResponseDto.of(isLiked, isBookmarked));
        }
    }

    @Transactional(readOnly = true)
    public Pack getPackDetailForAdmin(Long id) {
        Pack pack = packRepository.findWithItemsById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PACK_NOT_FOUND));
        pack.getPackHashtags().forEach(ph -> ph.getHashtag().getName());
        return pack;
    }

    @Transactional
    public Pack create(PackCreateRequestDto req, Member member) {

        Category category = findCategoryById(req.categoryId());

        List<Hashtag> hashtags = hashtagService.resolveHashtags(req.hashtagNames());

        Pack pack = Pack.builder()
                .category(category)
                .member(member)
                .name(req.name())
                .price(req.price())
                .mainImageUrl(req.mainImageUrl())
                .description(req.description())
                .hashtags(hashtags)
                .build();

        if (req.items() != null) {
            for (PackItemDto itemDto : req.items()) {
                PackItem item = PackItem.builder()
                        .pack(pack)
                        .name(itemDto.name())
                        .linkUrl(itemDto.linkUrl())
                        .description(itemDto.description())
                        .imageUrl(itemDto.imageUrl())
                        .build();
                pack.addItem(item);
            }
        }

        hashtagService.incrementUsageCount(hashtags);

        return packRepository.save(pack);
    }

    @Transactional
    public Pack update(Long id, PackUpdateRequestDto req, Member member) {
        Pack pack = findPackById(id);

        // 권한 체크
        validatePackOwnership(pack, member);

        Category newCategory = (req.categoryId() != null)
                ? findCategoryById(req.categoryId())
                : null;

        // Pack 기본 정보 업데이트
        pack.update(
                newCategory,
                req.name(),
                req.price(),
                req.mainImageUrl(),
                req.description()
        );

        pack.updateItems(req.items());

        List<Hashtag> hashtags = hashtagService.resolveHashtags(req.hashtagNames());

        HashtagUpdateResult result = pack.updateHashtag(hashtags);

        hashtagService.incrementUsageCount(result.added());
        hashtagService.decrementUsageCount(result.removed());

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
    public void delete(Long id, Member member) {
        Pack pack = findPackById(id);

        // 권한 체크
        validatePackOwnership(pack, member);

        List<Hashtag> hashtags = pack.getHashtags();
        hashtagService.decrementUsageCount(hashtags);

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

    @Transactional
    public PackLikeResponseDto togglePackLike(Long id, Member member) {
        Pack pack = findPackById(id);

        int deletedRows = packLikeRepository.deleteByPackAndMember(pack, member);

        if (deletedRows > 0) {
            packRepository.decrementLikeCount(id);
            entityManager.refresh(pack);
            return PackLikeResponseDto.unliked(pack.getPackLikeCount());
        } else {
            packLikeRepository.save(new PackLike(pack, member));
            packRepository.incrementLikeCount(id);
            entityManager.refresh(pack);
            return PackLikeResponseDto.liked(pack.getPackLikeCount());
        }
    }

    @Transactional
    public PackBookmarkResponseDto togglePackBookmark(Long id, Member member) {
        Pack pack = findPackById(id);

        int deletedRows = packBookmarkRepository.deleteByPackAndMember(pack, member);

        if (deletedRows > 0) {
            packRepository.decrementPackBookmarkCount(id);
            return PackBookmarkResponseDto.unbookmarked();
        } else {
            packBookmarkRepository.save(new PackBookmark(pack, member));
            packRepository.incrementPackBookmarkCount(id);
            return PackBookmarkResponseDto.bookmarked();
        }
    }

    @Transactional(readOnly = true)
    public Page<Member> getPackLikers(Long id, Pageable pageable) {
        Pack pack = findPackById(id);
        Page<PackLike> packLikes = packLikeRepository.findByPack(pack, pageable);

        return packLikes.map(PackLike::getMember);
    }

    @Transactional(readOnly = true)
    public Page<PackDetailResponseDto> getBookmarkedPacksByMember(Member member, Pageable pageable) {
        Page<PackBookmark> bookmarkPage = packBookmarkRepository.findByMemberOrderByCreatedAtDesc(member, pageable);

        List<Pack> packs = bookmarkPage.getContent().stream()
                .map(PackBookmark::getPack)
                .toList();

        if (packs.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Long> packIds = packs.stream().map(Pack::getId).toList();
        Set<Long> likedPackIds = packLikeRepository.findPackIdsByMemberAndPackIds(member, packIds);

        return bookmarkPage.map(bookmark -> {
            Pack pack = bookmark.getPack();

            boolean isLiked = likedPackIds.contains(pack.getId());
            return PackDetailResponseDto.forMember(
                    pack,
                    InteractionStatusResponseDto.of(isLiked, true) // (isLiked, isBookmarked = true)
            );
        });
    }

    private void validatePackOwnership(Pack pack, Member member) {
        // 관리자이거나 작성자 본인인 경우만 허용
        if (!member.getRole().equals(Role.ADMIN) && !pack.isOwner(member)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "Pack을 수정/삭제할 권한이 없습니다.");
        }
    }

    private List<PackDetailResponseDto> convertPacksToDto(List<Pack> packs, Member member) {
        if (member == null) {
            return packs.stream()
                    .map(PackDetailResponseDto::forAnonymous)
                    .toList();
        } else {
            List<Long> packIds = packs.stream().map(Pack::getId).toList();

            Set<Long> likedPackIds = packLikeRepository.findPackIdsByMemberAndPackIds(member,
                    packIds);

            Set<Long> bookmarkedPackIds = packBookmarkRepository.findPackIdsByMemberAndPackIds(
                    member, packIds);

            return packs.stream()
                    .map(pack -> {
                        boolean isLiked = likedPackIds.contains(pack.getId());
                        boolean isBookmarked = bookmarkedPackIds.contains(pack.getId());
                        return PackDetailResponseDto.forMember(
                                pack,
                                InteractionStatusResponseDto.of(isLiked, isBookmarked)
                        );
                    })
                    .toList();
        }
    }
}
