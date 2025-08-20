package com.starterpack.pack.entity;

import com.starterpack.category.entity.Category;
import com.starterpack.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pack")
public class Pack {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id") // ERD: category
    private Category category;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "total_cost")
    private Integer totalCost;

    @Column(name = "pack_like_count", nullable = false)
    private Integer packLikeCount = 0;

    @Column(length = 500)
    private String src;

    @Lob
    private String description;

    // pack_product 조인 테이블 매핑
    @ManyToMany
    @JoinTable(
            name = "pack_product",
            joinColumns = @JoinColumn(name = "pack_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Product> products = new HashSet<>();

    // 편의 메서드 (양쪽 연관관계 관리)
    public void addProduct(Product p) {
        products.add(p);
        p.getPacks().add(this);
    }
    public void removeProduct(Product p) {
        products.remove(p);
        p.getPacks().remove(this);
    }

    // --- getters/setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getTotalCost() { return totalCost; }
    public void setTotalCost(Integer totalCost) { this.totalCost = totalCost; }
    public Integer getPackLikeCount() { return packLikeCount; }
    public void setPackLikeCount(Integer packLikeCount) { this.packLikeCount = packLikeCount; }
    public String getSrc() { return src; }
    public void setSrc(String src) { this.src = src; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Set<Product> getProducts() { return products; }
}
