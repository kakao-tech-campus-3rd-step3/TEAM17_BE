package com.starterpack.member.controller;

import com.starterpack.member.dto.MemberCreateRequestDto;
import com.starterpack.member.dto.MemberResponseDto;
import com.starterpack.member.dto.MemberUpdateRequestDto;
import com.starterpack.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 멤버 생성
    @PostMapping
    public ResponseEntity<MemberResponseDto> addMember(
            @Valid @RequestBody MemberCreateRequestDto requestDto
    ) {
        MemberResponseDto responseDto = memberService.addMember(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 모든 멤버 조회
    @GetMapping
    public ResponseEntity<List<MemberResponseDto>> getAllMembers() {
        List<MemberResponseDto> members = memberService.findAllMembers();
        return ResponseEntity.status(HttpStatus.OK).body(members);
    }

    // ID로 멤버 조회
    @GetMapping("/{userId}")
    public ResponseEntity<MemberResponseDto> getMemberById(
            @PathVariable Long userId
    ) {
        MemberResponseDto responseDto = memberService.findMemberById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // 이메일로 멤버 조회
    @GetMapping("/email/{email}")
    public ResponseEntity<MemberResponseDto> getMemberByEmail(
            @PathVariable String email
    ) {
        MemberResponseDto responseDto = memberService.findMemberByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // 멤버 정보 수정
    @PutMapping("/{userId}")
    public ResponseEntity<MemberResponseDto> updateMember(
            @PathVariable Long userId,
            @Valid @RequestBody MemberUpdateRequestDto requestDto
    ) {
        MemberResponseDto responseDto = memberService.updateMember(userId, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // 멤버 삭제 (소프트 삭제)
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteMember(
            @PathVariable Long userId
    ) {
        memberService.deleteMember(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 멤버 완전 삭제
    @DeleteMapping("/{userId}/permanent")
    public ResponseEntity<Void> deleteMemberPermanently(
            @PathVariable Long userId
    ) {
        memberService.deleteMemberPermanently(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 멤버 활성화 상태 변경
    @PatchMapping("/{userId}/active")
    public ResponseEntity<MemberResponseDto> updateMemberActiveStatus(
            @PathVariable Long userId,
            @RequestParam Boolean isActive
    ) {
        MemberResponseDto responseDto = memberService.updateMemberActiveStatus(userId, isActive);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
