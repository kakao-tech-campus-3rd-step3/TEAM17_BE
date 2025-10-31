package com.starterpack.pack.controller;

import com.starterpack.auth.login.Login;
import com.starterpack.member.entity.Member;
import com.starterpack.pack.dto.PackBookmarkResponseDto;
import com.starterpack.pack.dto.PackCommentAddRequestDto;
import com.starterpack.pack.dto.PackCommentResponseDto;
import com.starterpack.pack.dto.PackCommentUpdateRequestDto;
import com.starterpack.pack.dto.PackCreateRequestDto;
import com.starterpack.pack.dto.PackDetailResponseDto;
import com.starterpack.pack.dto.PackLikeResponseDto;
import com.starterpack.pack.dto.PackLikerResponseDto;
import com.starterpack.pack.dto.PackRecommendDto;
import com.starterpack.pack.dto.PackUpdateRequestDto;
import com.starterpack.pack.entity.Pack;
import com.starterpack.pack.service.PackCommentService;
import com.starterpack.pack.service.PackRecommendService;
import com.starterpack.pack.service.PackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/starterPack")
@Tag(name = "Pack", description = "스타터팩 관리 API")
public class PackController {

    private final PackService packService;
    private final PackCommentService packCommentService;
    private final PackRecommendService packRecommendService;

    @GetMapping("/packs")
    @Operation(summary = "스타터팩 목록 조회", description = "모든 스타터팩 목록을 조회합니다.")
    public Map<String, List<PackDetailResponseDto>> getAllPacks() {
        List<Pack> packs = packService.getPacks(); // 엔티티 반환
        List<PackDetailResponseDto> responseDto = packs.stream()
                .map(PackDetailResponseDto::from)
                .toList();
        return Map.of("packs", responseDto);
    }

    @GetMapping("/categories/{categoryId}/packs")
    @Operation(summary = "카테고리별 스타터팩 조회", description = "특정 카테고리의 스타터팩 목록을 조회합니다.")
    public Map<String, List<PackDetailResponseDto>> listByCategory(
            @PathVariable @Positive Long categoryId
    ) {
        List<Pack> packs = packService.getPacksByCategory(categoryId); // 엔티티 반환
        List<PackDetailResponseDto> responseDto = packs.stream()
                .map(PackDetailResponseDto::from)
                .toList();
        return Map.of("packs", responseDto);
    }

    @GetMapping("/packs/{id}")
    @Operation(summary = "스타터팩 상세 조회", description = "특정 스타터팩의 상세 정보를 조회합니다.")
    public PackDetailResponseDto detail(@PathVariable @Positive Long id) {
        Pack pack = packService.getPackDetail(id); // 엔티티 반환
        return PackDetailResponseDto.from(pack);
    }

    @PostMapping("/packs")
    @Operation(summary = "스타터팩 생성", description = "새로운 스타터팩을 생성합니다.")
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<PackDetailResponseDto> create(
            @RequestBody @Valid PackCreateRequestDto req,
            @Login Member member
    ) {
        Pack created = packService.create(req, member); // 엔티티 반환
        PackDetailResponseDto body = PackDetailResponseDto.from(created);
        return ResponseEntity
                .created(URI.create("/api/starterPack/packs/" + body.id())) // base path 정합성
                .body(body);
    }

    @PatchMapping("/packs/{id}")
    @Operation(summary = "스타터팩 수정", description = "기존 스타터팩 정보를 수정합니다.")
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<PackDetailResponseDto> update(
            @PathVariable @Positive Long id,
            @RequestBody @Valid PackUpdateRequestDto req,
            @Login Member member
    ) {
        packService.update(id, req, member);
        Pack pack = packService.getPackDetail(id);
        PackDetailResponseDto response = PackDetailResponseDto.from(pack);

        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/packs/{id}")
    @Operation(summary = "스타터팩 삭제", description = "스타터팩을 삭제합니다.")
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<Void> delete(
            @PathVariable @Positive Long id,
            @Login Member member) {
        packService.delete(id, member);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/packs/{id}/like")
    @Operation(summary = "스타터팩 좋아요 토글", description = "스타터팩의 좋아요를 추가하거나 취소합니다.")
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<PackLikeResponseDto> togglePackLike(
            @PathVariable Long id,
            @Login Member member
    ){
        PackLikeResponseDto responseDto = packService.togglePackLike(id, member);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/packs/{id}/likes")
    @Operation(summary = "팩 좋아요 목록 조회", description = "팩에 좋아요를 누른 사용자 목록을 페이지로 반환합니다.")
    public ResponseEntity<Page<PackLikerResponseDto>> getPackLikers(
            @PathVariable
            Long id,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ){
        Page<Member> likers = packService.getPackLikers(id, pageable);
        Page<PackLikerResponseDto> responseDto = likers.map(PackLikerResponseDto::from);

        return ResponseEntity.ok(responseDto);
    }
    @PostMapping("/{packId}/comments")
    @Operation(
            summary = "댓글 작성",
            description = "지정된 팩에 댓글 또는 대댓글을 작성합니다. parentId가 없으면 루트 댓글, 있으면 대댓글로 처리됩니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<PackCommentResponseDto> addComment(
            @PathVariable Long packId,
            @Login Member member,
            @Valid @RequestBody PackCommentAddRequestDto requestDto
    ) {
        PackCommentResponseDto responseDto = packCommentService.addComment(packId, member, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{packId}/comments")
    @Operation(
            summary = "댓글 목록 조회",
            description = "지정된 팩의 댓글 목록을 페이지로 조회합니다. 기본 정렬은 생성일 오름차순(오래된순)입니다. 소프트 삭제된 댓글은 '삭제된 댓글입니다' 형태로 노출됩니다.")
    public ResponseEntity<Page<PackCommentResponseDto>> getComments(
            @PathVariable Long packId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<PackCommentResponseDto> page = packCommentService.getComments(packId, pageable);
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
        packCommentService.deleteComment(commentId, member);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/comments/{commentId}")
    @Operation(
            summary = "댓글 수정",
            description = "댓글 내용을 수정합니다. 작성자 본인 또는 관리자만 가능합니다."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<PackCommentResponseDto> updateComment(
            @PathVariable Long commentId,
            @Login Member member,
            @Valid @RequestBody PackCommentUpdateRequestDto requestDto
    ) {
        PackCommentResponseDto responseDto = packCommentService.updateComment(commentId, member, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/packs/{id}/bookmark")
    @Operation(summary = "스타터팩 북마크 토글", description = "유저가 스타터팩에 북마크를 추가하거나 취소합니다.")
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<PackBookmarkResponseDto> togglePackBookmark(
            @PathVariable Long id,
            @Login Member member
    ) {
        PackBookmarkResponseDto responseDto = packService.togglePackBookmark(id, member);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/packs/recommendations")
    @Operation(
            summary = "오늘의 추천 스타터팩 Top 3 조회",
            description = "추천 알고리즘으로 계산된 오늘의 스타터팩 3개를 반환 + 하루에 한 번 자동 갱신됩니다."
    )
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<List<PackRecommendDto>> getTodayRecommendations() {
        return ResponseEntity.ok(packRecommendService.getTodayTop3());
    }
}
