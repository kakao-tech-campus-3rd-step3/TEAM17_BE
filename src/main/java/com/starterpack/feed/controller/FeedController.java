package com.starterpack.feed.controller;

import com.starterpack.auth.login.Login;
import com.starterpack.feed.dto.FeedBookmarkResponseDto;
import com.starterpack.feed.dto.FeedCommentAddRequestDto;
import com.starterpack.feed.dto.FeedCommentResponseDto;
import com.starterpack.feed.dto.FeedCommentUpdateRequestDto;
import com.starterpack.feed.dto.FeedCreateRequestDto;
import com.starterpack.feed.dto.FeedLikeResponseDto;
import com.starterpack.feed.dto.FeedLikerResponseDto;
import com.starterpack.feed.dto.FeedResponseDto;
import com.starterpack.feed.dto.FeedUpdateRequestDto;
import com.starterpack.feed.entity.Feed;
import com.starterpack.feed.service.FeedCommentService;
import com.starterpack.feed.service.FeedService;
import com.starterpack.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feeds")
public class FeedController {
    private final FeedService feedService;
    private final FeedCommentService feedCommentService;


    public FeedController(FeedService feedService, FeedCommentService feedCommentService) {
        this.feedService = feedService;
        this.feedCommentService = feedCommentService;
    }

    @PostMapping
    @Operation(summary = "피드 생성", description = "새로운 피드를 생성합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<FeedResponseDto> addFeed (
            @Login Member member,
            @RequestBody FeedCreateRequestDto feedCreateDto
    ) {
        Feed feed = feedService.addFeed(member, feedCreateDto);

        FeedResponseDto responseDto = FeedResponseDto.from(feed);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{feedId}")
    @Operation(summary = "피드 상세 조회", description = "특정 피드의 상세 정보를 조회합니다.")
    public ResponseEntity<FeedResponseDto> getFeed (@PathVariable Long feedId) {
        Feed feed = feedService.getFeed(feedId);

        FeedResponseDto responseDto = FeedResponseDto.from(feed);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping
    @Operation(summary = "피드 목록 조회", description = "모든 피드 목록을 페이지로 조회합니다.")
    public ResponseEntity<Page<FeedResponseDto>> getAllFeeds(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Feed> feedPage = feedService.getAllFeeds(pageable);

        Page<FeedResponseDto> responseDto = feedPage.map(FeedResponseDto::from);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/{feedId}")
    @Operation(summary = "피드 수정", description = "기존 피드의 정보를 수정합니다.(상품 정보는 변경 불가)")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<FeedResponseDto> updateFeed(
            @PathVariable Long feedId,
            @Login Member member,
            @RequestBody FeedUpdateRequestDto feedUpdateRequestDto
    ) {
        Feed feed = feedService.updateFeed(feedId, member, feedUpdateRequestDto);

        FeedResponseDto responseDto = FeedResponseDto.from(feed);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/{feedId}")
    @Operation(summary = "피드 삭제", description = "특정 피드를 삭제합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> deleteFeed(
            @PathVariable Long feedId,
            @Login Member member
    ) {
        feedService.deleteFeed(feedId, member);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{feedId}/like")
    @Operation(summary = "피드 좋아요 토글", description = "피드의 좋아요를 추가하거나 취소합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<FeedLikeResponseDto> toggleFeedLike(
            @PathVariable Long feedId,
            @Login Member member
    ){
        FeedLikeResponseDto responseDto = feedService.toggleFeedLike(feedId, member);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{feedId}/likes")
    @Operation(summary = "피드 좋아요 목록 조회", description = "피드에 좋아요를 누른 사용자 목록을 페이지로 반환합니다.")
    public ResponseEntity<Page<FeedLikerResponseDto>> getFeedLikers(
            @PathVariable
            Long feedId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ){
        Page<Member> likers = feedService.getFeedLikers(feedId, pageable);
        Page<FeedLikerResponseDto> responseDto = likers.map(FeedLikerResponseDto::from);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/{feedId}/comments")
    @Operation(
            summary = "댓글 작성",
            description = "지정된 피드에 댓글 또는 대댓글을 작성합니다. parentId가 없으면 루트 댓글, 있으면 대댓글로 처리됩니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<FeedCommentResponseDto> addComment(
            @PathVariable Long feedId,
            @Login Member member,
            @Valid @RequestBody FeedCommentAddRequestDto requestDto
    ) {
        FeedCommentResponseDto responseDto = feedCommentService.addComment(feedId, member, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{feedId}/comments")
    @Operation(
            summary = "댓글 목록 조회",
            description = "지정된 피드의 댓글 목록을 페이지로 조회합니다. 기본 정렬은 생성일 오름차순(오래된순)입니다. 소프트 삭제된 댓글은 '삭제된 댓글입니다' 형태로 노출됩니다.")
    public ResponseEntity<Page<FeedCommentResponseDto>> getComments(
            @PathVariable Long feedId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<FeedCommentResponseDto> page = feedCommentService.getComments(feedId, pageable);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(
            summary = "댓글 소프트 삭제",
            description = "댓글을 소프트 삭제합니다. 작성자 본인 또는 관리자만 가능합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @Login Member member
    ) {
        feedCommentService.deleteComment(commentId, member);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/comments/{commentId}")
    @Operation(
            summary = "댓글 수정",
            description = "댓글 내용을 수정합니다. 작성자 본인 또는 관리자만 가능합니다."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<FeedCommentResponseDto> updateComment(
            @PathVariable Long commentId,
            @Login Member member,
            @Valid @RequestBody FeedCommentUpdateRequestDto requestDto
    ) {
        FeedCommentResponseDto responseDto = feedCommentService.updateComment(commentId, member, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/{feedId}/bookmark")
    @Operation(summary = "사용자 피드 북마크 토글", description = "유저가 피드에 북마크를 추가하거나 취소합니다.")
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<FeedBookmarkResponseDto> toggleFeedBookmark(
            @PathVariable Long feedId,
            @Login Member member
    ) {
        FeedBookmarkResponseDto responseDto = feedService.toggleFeedBookmark(feedId, member);
        return ResponseEntity.ok(responseDto);
    }
}
