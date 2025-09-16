package com.starterpack.member.controller;

import com.starterpack.auth.service.AuthService;
import com.starterpack.member.dto.MemberResponseDto;
import com.starterpack.member.dto.MemberUpdateRequestDto;
import com.starterpack.member.entity.Member;
import com.starterpack.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@Tag(name = "Member", description = "회원 관리 API")
public class MemberController {
    private final MemberService memberService;
    private final AuthService authService;

    public MemberController(MemberService memberService, AuthService authService) {
        this.memberService = memberService;
        this.authService = authService;
    }

    @GetMapping
    @Operation(summary = "회원 목록 조회", description = "모든 회원 목록을 조회합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<MemberResponseDto>> getAllMembers() {
        List<Member> members = memberService.findAllMembers();

        List<MemberResponseDto> responseDto = members.stream()
                .map(MemberResponseDto::new)
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "회원 상세 조회", description = "ID로 특정 회원의 상세 정보를 조회합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<MemberResponseDto> getMemberById(
            @PathVariable Long userId
    ) {
        MemberResponseDto responseDto = memberService.findMemberById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "이메일로 회원 조회", description = "이메일로 특정 회원의 정보를 조회합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<MemberResponseDto> getMemberByEmail(
            @PathVariable String email
    ) {
        MemberResponseDto responseDto = memberService.findMemberByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/{userId}")
    @Operation(summary = "회원 정보 수정", description = "기존 회원의 정보를 수정합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<MemberResponseDto> updateMember(
            @PathVariable Long userId,
            @Valid @RequestBody MemberUpdateRequestDto requestDto
    ) {
        MemberResponseDto responseDto = memberService.updateMember(userId, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "회원 삭제", description = "회원을 소프트 삭제합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> deleteMember(
            @PathVariable Long userId
    ) {
        memberService.deleteMember(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{userId}/permanent")
    @Operation(summary = "회원 완전 삭제", description = "회원을 데이터베이스에서 완전히 삭제합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> deleteMemberPermanently(
            @PathVariable Long userId
    ) {
        memberService.deleteMemberPermanently(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{userId}/active")
    @Operation(summary = "회원 활성화 상태 변경", description = "회원의 활성화 상태를 변경합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<MemberResponseDto> updateMemberActiveStatus(
            @PathVariable Long userId,
            @RequestParam Boolean isActive
    ) {
        MemberResponseDto responseDto = memberService.updateMemberActiveStatus(userId, isActive);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
