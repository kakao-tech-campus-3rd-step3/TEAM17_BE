package com.starterpack.pack.dto;

import com.starterpack.feed.dto.FeedCommentResponseDto.AuthorInfo;
import com.starterpack.member.entity.Member;
import com.starterpack.pack.entity.PackComment;
import java.time.LocalDateTime;

public record PackCommentResponseDto(
        Long id,
        Long parentId,
        String content,
        boolean isDeleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        AuthorInfo author,
        boolean isMine
) {
    public static PackCommentResponseDto from(PackComment entity, boolean isMine) {
        Member author = entity.getAuthor();

        return new PackCommentResponseDto(
                entity.getId(),
                entity.getParent() != null ? entity.getParent().getId() : null,
                entity.isDeleted() ? null : entity.getContent(),
                entity.isDeleted(),
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
}
