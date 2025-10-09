package com.starterpack.s3.dto;

/**
 * Presigned URL 발급을 요청할 때 사용할 DTO
 * @param fileName 업로드할 파일의 원본 이름
 */
public record PresignedUrlRequestDto(String fileName) {}
