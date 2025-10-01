package com.starterpack.linkpolicy.dto;

import com.starterpack.linkpolicy.model.LinkPolicy;
import java.time.LocalDateTime;

public record LinkPolicyResponseDto(
        Long id,
        String pattern,
        String description,
        LocalDateTime createdAt
) {
    public static LinkPolicyResponseDto from(LinkPolicy policy) {
        return new LinkPolicyResponseDto(
                policy.getId(),
                policy.getPattern(),
                policy.getDescription(),
                policy.getCreatedAt()
        );
    }
}


