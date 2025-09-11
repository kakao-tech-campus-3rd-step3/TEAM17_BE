package com.starterpack.feed.service;

import com.starterpack.category.entity.Category;
import com.starterpack.category.repository.CategoryRepository;
import com.starterpack.feed.dto.FeedCreateRequestDto;
import com.starterpack.feed.dto.FeedResponseDto;
import com.starterpack.feed.dto.FeedUpdateRequestDto;
import com.starterpack.feed.dto.ProductTagRequestDto;
import com.starterpack.feed.entity.Feed;
import com.starterpack.feed.entity.FeedProduct;
import com.starterpack.feed.entity.FeedType;
import com.starterpack.feed.repository.FeedRepository;
import com.starterpack.member.entity.Member;
import com.starterpack.product.entity.Product;
import com.starterpack.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Transactional
    public FeedResponseDto addFeed(
            Member member,
            FeedCreateRequestDto createDto) {
        Category category = getCategory(createDto.categoryId());

        Feed feed = Feed.builder()
                .user(member)
                .description(createDto.description())
                .imageUrl(createDto.imageUrl())
                .feedType(createDto.feedType())
                .category(category)
                .build();

        if (isInfoFeedWithProducts(createDto)) {
            createDto.products().forEach(productDto -> addProductToFeed(feed, productDto));
        }

        return FeedResponseDto.from(feedRepository.save(feed));
    }

    @Transactional(readOnly = true)
    public FeedResponseDto getFeed(Long feedId) {
        Feed feed = getFeedById(feedId);

        return FeedResponseDto.from(feed);
    }

    @Transactional(readOnly = true)
    public Page<FeedResponseDto> getAllFeeds(Pageable pageable) {
        Page<Feed> feedPage = feedRepository.findAll(pageable);

        return feedPage.map(FeedResponseDto::from);
    }

    @Transactional
    public FeedResponseDto updateFeed(
            Long feedId,
            Member member,
            FeedUpdateRequestDto updateDto
    ){
        Feed feed = getFeedById(feedId);

        checkFeedOwner(member, feed);

        Category category = getCategory(updateDto.categoryId());

        feed.update(updateDto.description(),
                updateDto.imageUrl(),
                category);

        return FeedResponseDto.from(feed);
    }

    @Transactional
    public void deleteFeed(Long feedId, Member member) {
        Feed feed = getFeedById(feedId);

        checkFeedOwner(member, feed);

        feedRepository.delete(feed);
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
    }

    private boolean isInfoFeedWithProducts(FeedCreateRequestDto createDto) {
        return createDto.feedType() == FeedType.INFO && createDto.products() != null && !createDto.products().isEmpty();
    }

    private void addProductToFeed(Feed feed, ProductTagRequestDto productDto) {
        Product product = new Product(
                productDto.name(),
                "link",
                "productType",
                productDto.imageUrl(),
                0,
                feed.getCategory()
        );

        productRepository.save(product);

        FeedProduct feedProduct = FeedProduct.builder()
                .feed(feed)
                .product(product)
                .description(productDto.description())
                .build();

        feed.getFeedProducts().add(feedProduct);
    }

    private void checkFeedOwner(Member member, Feed feed) {
        if (!feed.getUser().getUserId().equals(member.getUserId())) {
            throw new IllegalArgumentException("이 피드를 삭제할 권한이 없습니다.");
        }
    }

    private Feed getFeedById(Long feedId) {
        return feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("피드를 찾을 수 없습니다."));
    }

}
