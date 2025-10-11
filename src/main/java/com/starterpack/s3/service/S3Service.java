package com.starterpack.s3.service;

import com.starterpack.s3.dto.PresignedUrlsRequestDto;
import com.starterpack.s3.dto.PresignedUrlsResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public List<PresignedUrlsResponseDto> generatePresignedUrls(String dirName, PresignedUrlsRequestDto requestDto) {
        // 전달받은 파일 정보 리스트를 순회하며 각 파일에 대한 URL 생성
        return requestDto.files().stream()
                .map(fileInfo -> {
                    String fullPath = generateFullPath(dirName, fileInfo.fileName());

                    // Presigned URL 생성
                    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(fullPath)
                            .contentType(fileInfo.contentType())
                            .acl(ObjectCannedACL.PUBLIC_READ)
                            .build();

                    // Presigned URL 생성 요청 준비
                    PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                            .signatureDuration(Duration.ofMinutes(5)) // Presigned URL 유효 시간
                            .putObjectRequest(putObjectRequest)
                            .build();

                    String presignedUrl = s3Presigner.presignPutObject(presignRequest).url().toString();

                    // 최종 파일 URL 생성
                    String fileUrl = s3Client.utilities().getUrl(builder -> builder.bucket(bucket).key(fullPath)).toString();

                    return new PresignedUrlsResponseDto(presignedUrl, fileUrl);
                })
                .toList();
    }

    // 파일 경로를 생성하는 메서드
    private String generateFullPath(String dirName, String fileName) {
        String sanitizedDirName = dirName.replace("..", "").replace("/", "").replace("\\", "");
        return sanitizedDirName + "/" + UUID.randomUUID() + "_" + fileName;
    }
}
