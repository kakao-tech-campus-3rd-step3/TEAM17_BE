package com.starterpack.linkpolicy.dto;

import com.starterpack.linkpolicy.model.LinkPolicy;
import java.time.LocalDateTime;

public record LinkPolicyResponseDto(
        Long id,
        String pattern,
        LocalDateTime createdAt
) {
    public static LinkPolicyResponseDto from(LinkPolicy policy) {
        return new LinkPolicyResponseDto(
                policy.getId(),
                policy.getPattern(),
                policy.getCreatedAt()
        );
    }
}


