package com.starterpack.product.entity;

import com.starterpack.category.entity.Category;
import com.starterpack.pack.entity.Pack;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "product")
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 500)
    private String link;

    @Column(name = "product_type", length = 50)   // ERD: type
    private String productType;

    @Column(length = 500)
    private String src;

    @Column
    private Integer cost;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id") // ERD: category
    private Category category;

    public Product() {}

    public Product(Long id, String name, String link, String productType, String src, Integer cost, Category category) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.productType = productType;
        this.src = src;
        this.cost = cost;
        this.category = category;
    }

    public Product(String name, String link, String productType, String src, Integer cost, Category category) {
        this(null, name, link, productType, src, cost, category);
    }

    public static Product create(String name, String link, String productType, String src, Integer cost, Category category) {
        if (category == null) throw new IllegalArgumentException("Category must not be null");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Product name must not be blank");
        Product p = new Product();
        p.setName(name);
        p.setLink(link);
        p.setProductType(productType);
        p.setSrc(src);
        p.setCost(cost);
        p.setCategory(category);
        p.setLikeCount(0);
        return p;
    }

    public void changeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }
        this.name = name;
    }

    public void update(String name, String link, String productType, String src, Integer cost, Category category) {
        if (name != null) {
            changeName(name); // 이름 규칙을 도메인에서 보장
        }
        if (link != null && !link.isBlank()) {
            this.link = link;
        }
        if (productType != null && !productType.isBlank()) {
            this.productType = productType;
        }
        if (src != null && !src.isBlank()) {
            this.src = src;
        }
        if (cost != null) {
            if (cost < 0) throw new IllegalArgumentException("Product cost must be >= 0");
            this.cost = cost;
        }
        if (category != null) {
            this.category = category;
        }
    }
}
