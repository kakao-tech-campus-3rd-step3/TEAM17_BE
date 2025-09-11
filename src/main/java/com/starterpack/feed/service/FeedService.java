package com.starterpack.feed.service;

import com.starterpack.category.entity.Category;
import com.starterpack.category.repository.CategoryRepository;
import com.starterpack.feed.dto.FeedCreateRequestDto;
import com.starterpack.feed.dto.FeedResponseDto;
import com.starterpack.feed.dto.ProductTagRequestDto;
import com.starterpack.feed.entity.Feed;
import com.starterpack.feed.entity.FeedProduct;
import com.starterpack.feed.entity.FeedType;
import com.starterpack.feed.repository.FeedRepository;
import com.starterpack.member.entity.Member;
import com.starterpack.product.entity.Product;
import com.starterpack.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeedService {
    private final FeedRepository feedRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;


    public FeedService(FeedRepository feedRepository, CategoryRepository categoryRepository,
            ProductRepository productRepository) {
        this.feedRepository = feedRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

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
        Feed feed = feedRepository.findByIdWithDetails(feedId)
                .orElseThrow(() -> new IllegalArgumentException("피드를 찾지 못했습니다."));

        return FeedResponseDto.from(feed);
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
}
