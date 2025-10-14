package com.starterpack.s3.dto;

/**
 * Presigned URL 발급 후 응답으로 반환할 DTO
 * @param presignedUrl S3에 파일을 PUT 방식으로 업로드할 임시 URL
 * @param fileUrl      업로드 완료 후 DB에 저장될 최종 파일 URL
 */
public record PresignedUrlResponseDto(String presignedUrl, String fileUrl) {}
