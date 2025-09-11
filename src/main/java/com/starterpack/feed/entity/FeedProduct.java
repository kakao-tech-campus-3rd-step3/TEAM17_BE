package com.starterpack.feed.entity;

import com.starterpack.product.entity.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "feed_product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Lob
    private String description;

    @Builder
    public FeedProduct(Feed feed, Product product, String description) {
        this.feed = feed;
        this.product = product;
        this.description = description;
    }
}
