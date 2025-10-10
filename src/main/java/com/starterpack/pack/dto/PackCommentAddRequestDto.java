package com.starterpack.pack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PackCommentAddRequestDto(
        @NotBlank(message = "댓글 내용은 비어 있을 수 없습니다.")
        @Size(max = 500, message = "댓글은 500자를 넘을 수 없습니다.")
        String content,

        // 대댓글이 아니라면 null
        Long parentId
) {}