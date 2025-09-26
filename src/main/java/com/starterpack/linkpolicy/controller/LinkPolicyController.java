package com.starterpack.linkpolicy.controller;

import com.starterpack.linkpolicy.dto.LinkPolicyCreateRequestDto;
import com.starterpack.linkpolicy.dto.LinkPolicyDeleteRequestDto;
import com.starterpack.linkpolicy.dto.LinkPolicyResponseDto;
import com.starterpack.linkpolicy.service.LinkPolicyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/link-policies")
public class LinkPolicyController {

    private final LinkPolicyService linkPolicyService;

    public LinkPolicyController(LinkPolicyService linkPolicyService) {
        this.linkPolicyService = linkPolicyService;
    }

    // 정책 추가
    @PostMapping
    public ResponseEntity<LinkPolicyResponseDto> add(@Valid @RequestBody LinkPolicyCreateRequestDto request) {
        LinkPolicyResponseDto saved = linkPolicyService.add(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // 정책 삭제
    @DeleteMapping
    public ResponseEntity<Void> delete(@Valid @RequestBody LinkPolicyDeleteRequestDto request) {
        boolean deleted = linkPolicyService.delete(request.id());
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // 전체 조회
    @GetMapping
    public ResponseEntity<List<LinkPolicyResponseDto>> getAll() {
        return ResponseEntity.ok(linkPolicyService.getAll());
    }

}


