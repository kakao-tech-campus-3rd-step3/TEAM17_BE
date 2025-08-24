package com.starterpack.pack.controller;

import com.starterpack.pack.dto.PackCreateRequestDto;
import com.starterpack.pack.dto.PackDetailResponseDto;
import com.starterpack.pack.dto.PackResponseDto;
import com.starterpack.pack.dto.PackUpdateRequestDto;
import com.starterpack.pack.service.PackService;
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
public class PackController {

    private final PackService packService;

    /** 전체 리스트: GET  → {"packs":[ ... ]} */
    @GetMapping("/packs")
    public Map<String, List<PackResponseDto>> getAllPacks() {
        return Map.of("packs", packService.getPacks());
    }

    /** 카테고리별 리스트: GET  → {"packs":[ ... ]} */
    @GetMapping("/categories/{categoryId}/packs")
    public Map<String, List<PackResponseDto>> listByCategory(
            @PathVariable @Positive Long categoryId
    ) {
        return Map.of("packs", packService.getPacksByCategory(categoryId));
    }

    /** 상세: GET  → PackDetailResponseDto (parts는 객체로 권장) */
    @GetMapping("/packs/{id}")
    public PackDetailResponseDto detail(@PathVariable @Positive Long id) {
        return packService.getPackDetail(id);
    }

    /** 생성: POST  → 201 Created + Location + 생성된 상세 JSON */
    @PostMapping("/packs")
    public ResponseEntity<PackDetailResponseDto> create(
            @RequestBody @Valid PackCreateRequestDto req
    ) {
        PackDetailResponseDto created = packService.create(req);
        return ResponseEntity
                .created(URI.create("/api/v1/packs/" + created.id()))
                .body(created);
    }

    /** 수정: PATCH → 200 OK + 업데이트된 상세 JSON */
    @PatchMapping("/packs/{id}")
    public ResponseEntity<PackDetailResponseDto> update(
            @PathVariable @Positive Long id,
            @RequestBody @Valid PackUpdateRequestDto req
    ) {
        return ResponseEntity.ok(packService.update(id, req));
    }

    /** 삭제: DELETE  → 204 No Content */
    @DeleteMapping("/packs/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        packService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
