package com.starterpack.pack.entity;

import com.starterpack.category.entity.Category;
import com.starterpack.hashtag.dto.HashtagUpdateResult;
import com.starterpack.hashtag.entity.Hashtag;
import com.starterpack.member.entity.Member;
import com.starterpack.pack.dto.PackItemDto;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    @OneToMany(mappedBy = "pack", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PackHashtag> packHashtags = new ArrayList<>();

    public void changeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Pack name must not be blank");
        }
        this.name = name;
    }

    @Builder
    public Pack(Category category, Member member, String name, Integer price,
            String mainImageUrl, String description, List<Hashtag> hashtags) {
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
        setPackHashtags(hashtags);
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

    public void updateItems(List<PackItemDto> itemDtos) {
        if (itemDtos == null || itemDtos.isEmpty()) {
            return; // 또는 clearItems() 호출 여부 결정
        }

        // 기존 아이템 삭제
        this.clearItems();

        // 새 아이템 추가
        for (PackItemDto dto : itemDtos) {
            if (dto == null || dto.name() == null || dto.name().isBlank()) {
                continue; // null이나 빈 이름은 스킵
            }

            PackItem item = PackItem.builder()
                    .pack(this)
                    .name(dto.name())
                    .linkUrl(dto.linkUrl())
                    .description(dto.description())
                    .imageUrl(dto.imageUrl())
                    .build();
            this.addItem(item);
        }
    }

    // 권한 체크 메서드
    public boolean isOwner(Member member) {
        return member != null && this.member != null
                && this.member.getUserId() != null
                && this.member.getUserId().equals(member.getUserId());
    }

    public List<Hashtag> getHashtags() {
        return this.packHashtags.stream()
                .map(PackHashtag::getHashtag)
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

        setPackHashtags(newHashtagList);
        return new HashtagUpdateResult(added, removed);
    }

    private void setPackHashtags(List<Hashtag> hashtags) {
        this.packHashtags.clear();
        for (int i = 0; i < hashtags.size(); i++) {
            this.packHashtags.add(new PackHashtag(this, hashtags.get(i), i));
        }
    }
}
