package com.starterpack.pack.entity;

import com.starterpack.category.entity.Category;
import com.starterpack.member.entity.Member;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "pack")
public class Pack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "price")
    private Integer price;

    @Column(name = "pack_like_count", nullable = false)
    private Integer packLikeCount = 0;

    @Column(name = "pack_bookmark_count", nullable = false)
    private Integer packBookmarkCount = 0;

    @Column(name = "pack_comment_count", nullable = false)
    private Integer packCommentCount = 0;

    @Column(name = "main_image_url", length = 1000)
    private String mainImageUrl;

    @Lob
    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "pack", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PackItem> items = new ArrayList<>();

    public void changeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Pack name must not be blank");
        }
        this.name = name;
    }

    @Builder
    public Pack(Category category, Member member, String name, Integer price,
            String mainImageUrl, String description) {
        validateCreate(category, member, name);
        this.category = category;
        this.member = member;
        this.name = name;
        this.price = price;
        this.mainImageUrl = mainImageUrl;
        this.description = description;
        this.packLikeCount = 0;
        this.packBookmarkCount = 0;
        this.packCommentCount = 0;
    }

    private void validateCreate(Category category, Member member, String name) {
        if (category == null) {
            throw new IllegalArgumentException("Category must not be null");
        }
        if (member == null) {
            throw new IllegalArgumentException("Member must not be null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Pack name must not be blank");
        }
    }

    // PackItem 관리 메서드
    public void addItem(PackItem item) {
        items.add(item);
        item.setPack(this);
    }

    public void clearItems() {
        items.clear();
    }

    // 수정 메서드
    public void update(Category category, String name, Integer price,
            String mainImageUrl, String description) {
        if (category != null) {
            this.category = category;
        }
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (price != null) {
            this.price = price;
        }
        if (mainImageUrl != null) {
            this.mainImageUrl = mainImageUrl;
        }
        if (description != null) {
            this.description = description;
        }
    }

    // 권한 체크 메서드
    public boolean isOwner(Member member) {
        return this.member.getUserId().equals(member.getUserId());
    }

}
