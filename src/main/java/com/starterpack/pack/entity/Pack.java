package com.starterpack.pack.entity;

import com.starterpack.category.entity.Category;
import com.starterpack.product.entity.Product;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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

    @Column(name = "pack_bookmark_count", nullable = false)
    private Integer packBookmarkCount = 0;

    @Column(name = "pack_comment_count", nullable = false)
    private Integer packCommentCount = 0;

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

    public void addProduct(Product p) {
        products.add(p);
        p.getPacks().add(this);
    }
    public void removeProduct(Product p) {
        products.remove(p);
        p.getPacks().remove(this);
    }
    public void changeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Pack name must not be blank");
        }
        this.name = name;
    }

    public int calcTotalCost() {
        int sum = 0;
        for (Product pr : this.products) {
            if (pr.getCost() != null) sum += pr.getCost();
        }
        return sum;
    }

    public static Pack create(
            Category category,
            String name,
            String description,
            String src,
            Set<Product> products,
            Integer requestedTotalCost
    ) {
        Pack p = new Pack();
        if (category == null) {
            throw new IllegalArgumentException("Category must not be null");
        }
        p.setCategory(category);
        p.changeName(name);
        p.setDescription(description);
        p.setSrc(src);
        p.setPackLikeCount(0);
        p.setPackBookmarkCount(0);
        p.setPackCommentCount(0);

        if (products != null) {
            for (Product pr : products) {
                p.addProduct(pr);
            }
        }
        p.setTotalCost(requestedTotalCost != null ? requestedTotalCost : p.calcTotalCost());
        return p;
    }

    public void applyUpdate(
            Category newCategory,
            String newName,
            Set<Product> newProducts,
            Integer requestedTotalCost,
            String newDescription,
            String newSrc
    ) {
        if (newCategory != null) this.setCategory(newCategory);
        if (newName != null) this.changeName(newName);
        if (newDescription != null) this.setDescription(newDescription);
        if (newSrc != null) this.setSrc(newSrc);

        if (newProducts != null) {
            for (Product pr : new java.util.HashSet<>(this.products)) {
                this.removeProduct(pr);
            }
            for (Product pr : newProducts) {
                this.addProduct(pr);
            }
            this.setTotalCost(requestedTotalCost != null ? requestedTotalCost : this.calcTotalCost());
        } else if (requestedTotalCost != null) {
            this.setTotalCost(requestedTotalCost);
        }
    }
}
