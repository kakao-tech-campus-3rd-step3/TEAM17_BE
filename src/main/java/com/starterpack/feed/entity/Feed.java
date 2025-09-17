package com.starterpack.feed.entity;

import com.starterpack.category.entity.Category;
import com.starterpack.member.entity.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name="feed")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member user;

    @Lob
    private String description;

    @Column(name = "image_url", length = 500, nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "feed_type", nullable = false)
    private FeedType feedType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "like_count", nullable = false)
    private int likeCount = 0;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedProduct> feedProducts = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Feed(Member user, String description, String imageUrl, FeedType feedType, Category category) {
        this.user = user;
        this.description = description;
        this.imageUrl = imageUrl;
        this.feedType = feedType;
        this.category = category;
    }

    public void update(String description, String imageUrl, Category category) {
        if (description != null) {
            this.description = description;
        }
        if (imageUrl != null) {
            this.imageUrl = imageUrl;
        }
        if (category != null) {
            this.category = category;
        }
    }
}
