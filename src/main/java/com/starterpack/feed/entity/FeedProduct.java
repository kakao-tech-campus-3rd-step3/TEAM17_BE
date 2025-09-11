package com.starterpack.feed.entity;

import com.starterpack.product.entity.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "feed_product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedProduct {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Lob
    private String description;
}
