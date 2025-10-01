package com.starterpack.feed.dto;

import com.starterpack.feed.entity.FeedComment;
import com.starterpack.member.entity.Member;
import java.time.LocalDateTime;

public record FeedCommentResponseDto(
        Long id,
        Long parentId,
        String content,
        boolean isDeleted,
        String deletedBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        AuthorInfo author,
        boolean isMine
) {
    public static FeedCommentResponseDto from(FeedComment entity, boolean isMine) {
        Member author = entity.getAuthor();
        return new FeedCommentResponseDto(
                entity.getId(),
                entity.getParent() != null ? entity.getParent().getId() : null,
                entity.isDeleted() ? null : entity.getContent(),
                entity.isDeleted(),
                entity.getDeletedBy() != null ? entity.getDeletedBy().name() : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                new AuthorInfo(
                        author.getUserId(),
                        author.getName(),
                        author.getProfileImageUrl()
                ),
                isMine
        );
    }

    public record AuthorInfo(Long id, String name, String profileImageUrl) {}
}
