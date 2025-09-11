package com.starterpack.feed.controller;

import com.starterpack.auth.CustomMemberDetails;
import com.starterpack.feed.dto.FeedCreateRequestDto;
import com.starterpack.feed.dto.FeedResponseDto;
import com.starterpack.feed.service.FeedService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
            @AuthenticationPrincipal CustomMemberDetails customMemberDetails,
            @RequestBody FeedCreateRequestDto feedCreateDto
    ) {
        FeedResponseDto responseDto = feedService.addFeed(customMemberDetails.getMember(), feedCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{feedId}")
    public ResponseEntity<FeedResponseDto> getFeed (@PathVariable Long feedId) {
        FeedResponseDto responseDto = feedService.getFeed(feedId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

}
