package com.starterpack.feed.entity;
import com.starterpack.exception.BusinessException;
import com.starterpack.exception.ErrorCode;
import com.starterpack.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "feed_comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class FeedComment {

    public static final int MAX_DEPTH = 1; // 0: 루트 댓글, 1: 대댓글

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private FeedComment parent;

    @OneToMany(mappedBy = "parent")
    @Builder.Default
    private List<FeedComment> children = new ArrayList<>();

    @Column(name = "depth", nullable = false)
    private int depth;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    public static FeedComment createRoot(Feed feed, Member author, String content) {
        validateArgs(feed, author, content);
        return FeedComment.builder()
                .feed(feed)
                .author(author)
                .content(content)
                .depth(0)
                .deleted(false)
                .build();
    }

    public static FeedComment createReply(Feed feed, Member author, String content, FeedComment parent) {
        validateArgs(feed, author, content);
        if (parent == null)
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "parent is null for reply");
        int nextDepth = parent.depth + 1;
        if (nextDepth > MAX_DEPTH) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "대댓글은 최대 1단계까지만 허용합니다.");
        }
        FeedComment reply = FeedComment.builder()
                .feed(feed)
                .author(author)
                .content(content)
                .parent(parent)
                .depth(nextDepth)
                .deleted(false)
                .build();
        parent.children.add(reply);
        return reply;
    }

    private static void validateArgs(Feed feed, Member author, String content) {
        if (feed == null || author == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "feed/author must not be null");
        }
        if (content == null || content.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "content must not be blank");
        }
        if (content.length() > 500) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "content length must be <= 500");
        }
    }

    public void updateContent(String newContent) {
        if (this.deleted) {
            throw new BusinessException(ErrorCode.COMMENT_ALREADY_DELETED, "삭제된 댓글은 수정할 수 없습니다.");
        }
        if (newContent == null || newContent.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "content must not be blank");
        }
        String trimmed = newContent.strip();
        if (trimmed.length() > 500) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "content length must be <= 500");
        }
        this.content = trimmed;
    }

    public void validateOwner(Member member) {
        if (member == null || !this.author.getUserId().equals(member.getUserId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
    }

    public void softDelete() {
        if (this.deleted) return;
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
        this.content = ""; // UI에서 "삭제된 댓글입니다" 식으로 표시
    }

    /** 필요 시 복구 기능 유지 */
    public void restoreByAdmin(String restoredContent) {
        this.deleted = false;
        this.deletedAt = null;
        this.content = restoredContent;
    }
}
