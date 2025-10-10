package com.starterpack.pack.service;

import com.starterpack.exception.BusinessException;
import com.starterpack.exception.ErrorCode;
import com.starterpack.pack.dto.PackCommentAddRequestDto;
import com.starterpack.pack.dto.PackCommentResponseDto;
import com.starterpack.pack.dto.PackCommentUpdateRequestDto;
import com.starterpack.pack.entity.Pack;
import com.starterpack.pack.entity.PackComment;
import com.starterpack.pack.repository.PackCommentRepository;
import com.starterpack.pack.repository.PackRepository;
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
public class PackCommentService {

    private final PackRepository packRepository;
    private final PackCommentRepository packCommentRepository;

    @Transactional
    public PackCommentResponseDto addComment(Long packId, Member member, PackCommentAddRequestDto req) {
        Pack pack = packRepository.findById(packId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PACK_NOT_FOUND));

        PackComment comment;
        if (req.parentId() == null) {
            comment = PackComment.createRoot(pack, member, sanitize(req.content()));
        } else {
            PackComment parent = packCommentRepository.findById(req.parentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST));
            if (!parent.getPack().getId().equals(pack.getId())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST);
            }
            comment = PackComment.createReply(pack, member, sanitize(req.content()), parent);
        }

        PackComment saved = packCommentRepository.save(comment);
        packRepository.incrementCommentCount(packId);
        return PackCommentResponseDto.from(saved, true);
    }

    public Page<PackCommentResponseDto> getComments(Long packId, Pageable pageable) {
        if (!packRepository.existsById(packId)) {
            throw new BusinessException(ErrorCode.PACK_NOT_FOUND);
        }
        Page<PackComment> page = packCommentRepository.findByPackId(packId, pageable);
        return page.map(c -> PackCommentResponseDto.from(c, false));
    }

    /** 댓글 소프트 삭제 (작성자 또는 관리자) */
    @Transactional
    public void deleteComment(Long commentId, Member member) {
        PackComment comment = packCommentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST));

        if (!isOwner(member, comment) && !isAdmin(member)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        if (comment.isDeleted()) {
            return; // 멱등성
        }

        Long packId = comment.getPack().getId();
        comment.softDelete();
        packRepository.decrementCommentCount(packId);
    }

    @Transactional
    public PackCommentResponseDto updateComment(Long commentId, Member member, PackCommentUpdateRequestDto requestDto) {
        PackComment comment = packCommentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if (comment.isDeleted()) {
            throw new BusinessException(ErrorCode.COMMENT_ALREADY_DELETED);
        }

        comment.validateOwner(member);
        comment.updateContent(requestDto.content());

        boolean isMine = comment.getAuthor().getUserId().equals(member.getUserId());
        return PackCommentResponseDto.from(comment, isMine);
    }

    private static boolean isOwner(Member member, PackComment comment) {
        return member != null && comment.getAuthor().getUserId().equals(member.getUserId());
    }

    private static boolean isAdmin(Member member) {
        try {
            return member != null && member.getRole() == Role.ADMIN;
        } catch (Exception e) {
            return false;
        }
    }

    /** 간단한 서버측 sanitize 훅 */
    private static String sanitize(String raw) {
        if (raw == null) return null;
        String trimmed = raw.trim();
        return trimmed.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
}
