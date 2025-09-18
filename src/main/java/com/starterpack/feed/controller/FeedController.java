package com.starterpack.feed.controller;

import com.starterpack.auth.CustomMemberDetails;
import com.starterpack.auth.login.Login;
import com.starterpack.feed.dto.FeedCreateRequestDto;
import com.starterpack.feed.dto.FeedLikeResponseDto;
import com.starterpack.feed.dto.FeedResponseDto;
import com.starterpack.feed.dto.FeedUpdateRequestDto;
import com.starterpack.feed.entity.Feed;
import com.starterpack.feed.service.FeedService;
import com.starterpack.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<FeedResponseDto> addFeed (
            @Login Member member,
            @RequestBody FeedCreateRequestDto feedCreateDto
    ) {
        Feed feed = feedService.addFeed(member, feedCreateDto);

        FeedResponseDto responseDto = FeedResponseDto.from(feed);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{feedId}")
    public ResponseEntity<FeedResponseDto> getFeed (@PathVariable Long feedId) {
        Feed feed = feedService.getFeed(feedId);

        FeedResponseDto responseDto = FeedResponseDto.from(feed);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<Page<FeedResponseDto>> getAllFeeds(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Feed> feedPage = feedService.getAllFeeds(pageable);

        Page<FeedResponseDto> responseDto = feedPage.map(FeedResponseDto::from);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/{feedId}")
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
    public ResponseEntity<Void> deleteFeed(
            @PathVariable Long feedId,
            @AuthenticationPrincipal CustomMemberDetails customMemberDetails
    ) {
        feedService.deleteFeed(feedId, customMemberDetails.getMember());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{feedId}/like")
    @Operation(summary = "피드 좋아요 토글", description = "피드의 좋아요를 추가하거나 취소합니다.")
    public ResponseEntity<FeedLikeResponseDto> toggleFeedLike(
            @PathVariable Long feedId,
            @Login Member member
    ){
        FeedLikeResponseDto responseDto = feedService.toggleFeedLike(feedId, member);
        return ResponseEntity.ok(responseDto);
    }
}
