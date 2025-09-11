package com.starterpack.product.entity;

import com.starterpack.category.entity.Category;
import com.starterpack.pack.entity.Pack;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
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

    // pack_product 역방향 (선택)
    @ManyToMany(mappedBy = "products")
    private Set<Pack> packs = new HashSet<>();

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

    public void update(String name, String link, String productType, String src, Integer cost, Category category) {
        if (name != null && !name.isBlank()) {
            this.name = name;
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
            this.cost = cost;
        }
        if (category != null) {
            this.category = category;
        }
    }

    // --- getters/setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }
    public String getSrc() { return src; }
    public void setSrc(String src) { this.src = src; }
    public Integer getCost() { return cost; }
    public void setCost(Integer cost) { this.cost = cost; }
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public Set<Pack> getPacks() { return packs; }
}
