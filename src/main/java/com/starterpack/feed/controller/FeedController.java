package com.starterpack.feed.controller;

import com.starterpack.auth.login.Login;
import com.starterpack.feed.dto.FeedCreateRequestDto;
import com.starterpack.feed.dto.FeedLikeResponseDto;
import com.starterpack.feed.dto.FeedLikerResponseDto;
import com.starterpack.feed.dto.FeedResponseDto;
import com.starterpack.feed.dto.FeedUpdateRequestDto;
import com.starterpack.feed.entity.Feed;
import com.starterpack.feed.service.FeedService;
import com.starterpack.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
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
}
