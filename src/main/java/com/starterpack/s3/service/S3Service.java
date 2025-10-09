package com.starterpack.s3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // Presigned URL을 생성 후 반환하는 메소드
    public String generatePresignedUrl(String dirName, String fileName, String contentType) {
        // 파일 이름이 겹치지 않도록 고유한 경로 생성
        String fullPath = dirName + "/" + UUID.randomUUID() + "_" + fileName;

        // S3에 파일을 올리는 'PUT' 요청을 미리 준비
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fullPath)
                .contentType(contentType)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        // Presigned URL 생성 요청 준비
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5)) // Presigned URL 유효 시간
                .putObjectRequest(putObjectRequest)
                .build();

        // S3Presigner를 사용하여 Presigned URL 생성 후 문자열로 반환
        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }
}
