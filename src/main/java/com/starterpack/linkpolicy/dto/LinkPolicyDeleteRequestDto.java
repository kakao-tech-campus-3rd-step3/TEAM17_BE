package com.starterpack.linkpolicy.dto;

import jakarta.validation.constraints.NotNull;

public record LinkPolicyDeleteRequestDto(
        @NotNull Long id
) {}


