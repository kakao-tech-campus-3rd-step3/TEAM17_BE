package com.starterpack.s3.dto;

import java.util.List;

public record PresignedUrlsRequestDto(
        List<FileInfo> files
) {
    public record FileInfo(String fileName, String contentType) {}
}
