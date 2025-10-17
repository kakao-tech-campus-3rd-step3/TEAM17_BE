package com.starterpack.feed.entity;

import com.starterpack.category.entity.Category;
import com.starterpack.exception.BusinessException;
import com.starterpack.exception.ErrorCode;
import com.starterpack.hashtag.dto.HashtagUpdateResult;
import com.starterpack.hashtag.entity.Hashtag;
import com.starterpack.member.entity.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    @Column(length = 2000)
    private String description;

    @Column(name = "image_url", length = 500, nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "like_count", nullable = false)
    private long likeCount = 0;

    @Column(name = "bookmark_count", nullable = false)
    private long bookmarkCount = 0;

    @Column(name = "comment_count", nullable = false)
    private long commentCount = 0;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("tagOrder ASC")
    private List<FeedHashtag> feedHashtags = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;



    @Builder
    public Feed(Member user, String description, String imageUrl, Category category, List<Hashtag> hashtags) {
        this.user = user;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        setFeedHashtags(hashtags);
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

    public List<Hashtag> getHashtags() {
        return this.feedHashtags.stream()
                .map(FeedHashtag::getHashtag)
                .toList();
    }

    public HashtagUpdateResult updateHashtag(List<Hashtag> newHashtagList) {
        if (newHashtagList == null) {
            return HashtagUpdateResult.EMPTY_HASHTAG;
        }

        Set<Hashtag> oldHashtags = new HashSet<>(this.getHashtags());
        Set<Hashtag> newHashtags = new HashSet<>(newHashtagList);

        Set<Hashtag> added = new HashSet<>(newHashtags);
        added.removeAll(oldHashtags);

        Set<Hashtag> removed = new HashSet<>(oldHashtags);
        removed.removeAll(newHashtags);

        setFeedHashtags(newHashtagList);
        return new HashtagUpdateResult(added, removed);
    }

    public void validateOwner(Member member) {
        if (!this.user.getUserId().equals(member.getUserId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
    }

    private void setFeedHashtags(List<Hashtag> hashtags) {
        this.feedHashtags.clear();
        for (int i = 0; i < hashtags.size(); i++) {
            this.feedHashtags.add(new FeedHashtag(this, hashtags.get(i), i));
        }
    }
}
