package com.starterpack.member.controller;

import com.starterpack.auth.login.Login;
import com.starterpack.member.dto.MyPageResponseDto;
import com.starterpack.member.entity.Member;
import com.starterpack.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@Tag(name = "MyPage", description = "마이페이지 API")
public class MyPageController {
    private final MemberService memberService;

    public MyPageController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/{userId}/mypage")
    @Operation(
            summary = "마이페이지 조회", 
            description = """
                    특정 회원의 마이페이지 정보를 조회합니다.
                    
                    **인증**: 비로그인 사용자도 조회 가능 (토큰 없이도 요청 가능)
                    
                    **응답 정보**:
                    - 사용자 기본 정보 (닉네임, 프로필 이미지, 취미, 한 줄 소개)
                    - 게시물 통계 (팩 개수, 피드 개수, 전체 게시물 수)
                    - 작성한 팩 목록 (최신순, 제목, 이미지)
                    - 작성한 피드 목록 (최신순, 설명, 이미지)
                    - isMe: 조회한 사람이 본인인지 여부 (로그인 필수)
                    
                    **예시**:
                    - 비로그인 사용자가 조회: isMe = false
                    - 다른 사용자가 조회: isMe = false
                    - 본인이 조회: isMe = true
                    """
    )
    @Parameter(
            name = "userId", 
            description = "조회할 회원의 ID",
            required = true,
            example = "1"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "마이페이지 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "404", 
                    description = "회원을 찾을 수 없음"
            )
    })
    public ResponseEntity<MyPageResponseDto> getMyPage(
            @PathVariable Long userId,
            @Login(required = false) Member currentMember
    ) {
        Long currentUserId = (currentMember != null) ? currentMember.getUserId() : null;
        MyPageResponseDto responseDto = memberService.getMyPage(userId, currentUserId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}

