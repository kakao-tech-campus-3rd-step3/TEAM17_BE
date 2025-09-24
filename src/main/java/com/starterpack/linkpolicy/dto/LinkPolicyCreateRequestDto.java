package com.starterpack.linkpolicy.dto;

import jakarta.validation.constraints.NotBlank;

public record LinkPolicyCreateRequestDto(
        @NotBlank String pattern
) {}


