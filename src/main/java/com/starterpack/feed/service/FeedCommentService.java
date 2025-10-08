package com.starterpack.feed.service;

import com.starterpack.exception.BusinessException;
import com.starterpack.exception.ErrorCode;
import com.starterpack.feed.dto.FeedCommentAddRequestDto;
import com.starterpack.feed.dto.FeedCommentResponseDto;
import com.starterpack.feed.dto.FeedCommentUpdateRequestDto;
import com.starterpack.feed.entity.Feed;
import com.starterpack.feed.entity.FeedComment;
import com.starterpack.feed.repository.FeedCommentRepository;
import com.starterpack.feed.repository.FeedRepository;
import com.starterpack.member.entity.Member;
import com.starterpack.member.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedCommentService {

    private final FeedRepository feedRepository;
    private final FeedCommentRepository feedCommentRepository;

    /**
     * 댓글/대댓글 작성
     */
    @Transactional
    public FeedCommentResponseDto addComment(Long feedId, Member member, FeedCommentAddRequestDto req) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FEED_NOT_FOUND));

        FeedComment comment;
        if (req.parentId() == null) {
            comment = FeedComment.createRoot(feed, member, sanitize(req.content()));
        } else {
            FeedComment parent = feedCommentRepository.findById(req.parentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST));
            // 부모가 다른 피드에 속한 경우 방지
            if (!parent.getFeed().getId().equals(feed.getId())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST);
            }
            comment = FeedComment.createReply(feed, member, sanitize(req.content()), parent);
        }

        FeedComment saved = feedCommentRepository.save(comment);

        feedRepository.incrementCommentCount(feedId);

        return FeedCommentResponseDto.from(saved, /*isMine*/ true);
    }

    /**
     * 피드별 댓글 목록 조회 (소프트 삭제된 항목은 tombstone 표시를 위해 그대로 반환)
     */
    public Page<FeedCommentResponseDto> getComments(Long feedId, Pageable pageable) {
        // 피드 존재 여부 보장 (권장: 캐시/existsById 활용 가능)
        if (!feedRepository.existsById(feedId)) {
            throw new BusinessException(ErrorCode.FEED_NOT_FOUND);
        }
        Page<FeedComment> page = feedCommentRepository.findByFeedId(feedId, pageable);
        return page.map(c -> FeedCommentResponseDto.from(c, /*isMine*/ false));
    }

    /**
     * 댓글 소프트 삭제 (작성자 또는 관리자)
     */
    @Transactional
    public void deleteComment(Long commentId, Member member) {
        FeedComment comment = feedCommentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST));

        if (!isOwner(member, comment) && !isAdmin(member)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        if (comment.isDeleted()) {
            return;
        }
        Long feedId = comment.getFeed().getId();
        comment.softDelete();

        feedRepository.decrementCommentCount(feedId);
    }

    private static boolean isOwner(Member member, FeedComment comment) {
        return member != null && comment.getAuthor().getUserId().equals(member.getUserId());
    }

    private static boolean isAdmin(Member member) {
        try {
            return member != null && member.getRole() == Role.ADMIN;
        } catch (Exception e) {
            return false;
        }
    }


    @Transactional
    public FeedCommentResponseDto updateComment(Long commentId, Member member, FeedCommentUpdateRequestDto requestDto) {
        FeedComment comment = feedCommentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if (comment.isDeleted()) {
            throw new BusinessException(ErrorCode.COMMENT_ALREADY_DELETED);
        }

        comment.validateOwner(member);
        comment.updateContent(requestDto.content());

        boolean isMine = comment.getAuthor().getUserId().equals(member.getUserId());
        return FeedCommentResponseDto.from(comment, isMine);
    }

    /** 간단한 서버측 sanitize 훅 (필요시 확장) */
    private static String sanitize(String raw) {
        if (raw == null) return null;
        String trimmed = raw.trim();
        return trimmed.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

}
