package com.starterpack.pack.controller;

import com.starterpack.pack.dto.PackCreateRequestDto;
import com.starterpack.pack.dto.PackDetailResponseDto;
import com.starterpack.pack.dto.PackResponseDto;
import com.starterpack.pack.dto.PackUpdateRequestDto;
import com.starterpack.pack.entity.Pack;
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

    @GetMapping("/packs")
    @Operation(summary = "스타터팩 목록 조회", description = "모든 스타터팩 목록을 조회합니다.")
    public Map<String, List<PackResponseDto>> getAllPacks() {
        List<Pack> packs = packService.getPacks(); // 엔티티 반환
        List<PackResponseDto> responseDto = packs.stream()
                .map(PackResponseDto::from)
                .toList();
        return Map.of("packs", responseDto);
    }

    @GetMapping("/categories/{categoryId}/packs")
    @Operation(summary = "카테고리별 스타터팩 조회", description = "특정 카테고리의 스타터팩 목록을 조회합니다.")
    public Map<String, List<PackResponseDto>> listByCategory(
            @PathVariable @Positive Long categoryId
    ) {
        List<Pack> packs = packService.getPacksByCategory(categoryId); // 엔티티 반환
        List<PackResponseDto> responseDto = packs.stream()
                .map(PackResponseDto::from)
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
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<PackDetailResponseDto> create(
            @RequestBody @Valid PackCreateRequestDto req
    ) {
        Pack created = packService.create(req); // 엔티티 반환
        PackDetailResponseDto body = PackDetailResponseDto.from(created);
        return ResponseEntity
                .created(URI.create("/api/starterPack/packs/" + body.id())) // base path 정합성
                .body(body);
    }

    @PatchMapping("/packs/{id}")
    @Operation(summary = "스타터팩 수정", description = "기존 스타터팩 정보를 수정합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<PackDetailResponseDto> update(
            @PathVariable @Positive Long id,
            @RequestBody @Valid PackUpdateRequestDto req
    ) {
        Pack updated = packService.update(id, req); // 엔티티 반환
        return ResponseEntity.ok(PackDetailResponseDto.from(updated));
    }

    @DeleteMapping("/packs/{id}")
    @Operation(summary = "스타터팩 삭제", description = "스타터팩을 삭제합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        packService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
