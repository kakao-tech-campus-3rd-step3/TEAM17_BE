package com.starterpack.s3.service;

import com.starterpack.exception.BusinessException;
import com.starterpack.exception.ErrorCode;
import com.starterpack.s3.dto.PresignedUrlsRequestDto;
import com.starterpack.s3.dto.PresignedUrlsResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import java.time.Duration;
import java.util.UUID;

@Slf4j
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
                    try {
                        String fullPath = generateFullPath(dirName, fileInfo.fileName());

                        String presignedUrl = generatePresignedUrl(fullPath, fileInfo.contentType());

                        String fileUrl = generateFileUrl(fullPath);

                        return new PresignedUrlsResponseDto(presignedUrl, fileUrl);

                    } catch (BusinessException e) {
                        throw e;
                    } catch (Exception e) {
                        log.error("파일 처리 중 예상치 못한 오류 - fileName: {}, error: {}",
                                fileInfo.fileName(), e.getMessage(), e);
                        throw new BusinessException(
                                ErrorCode.S3_UPLOAD_FAILED,
                                "파일 '" + fileInfo.fileName() + "' 처리 중 오류가 발생했습니다."
                        );
                    }
                }).toList();
    }

    /**
     * 파일 경로 생성
     */
    private String generateFullPath(String dirName, String fileName) {
        try {
            if (dirName == null || dirName.isBlank()) {
                throw new BusinessException(
                        ErrorCode.INVALID_FILE_PATH,
                        "디렉토리 이름이 제공되지 않았습니다."
                );
            }

            if (fileName == null || fileName.isBlank()) {
                throw new BusinessException(
                        ErrorCode.INVALID_FILE_PATH,
                        "파일 이름이 제공되지 않았습니다."
                );
            }

            String sanitizedDirName = dirName.replace("..", "")
                    .replace("\0", "")
                    .replace("/", "")
                    .replace("\\", "")
                    .replaceAll("[:\\*\\?\"<>|]", "")
                    .trim();

            if (sanitizedDirName.isEmpty()) {
                throw new BusinessException(
                        ErrorCode.INVALID_FILE_PATH,
                        "디렉토리 이름이 유효하지 않습니다."
                );
            }

            return sanitizedDirName + "/" + UUID.randomUUID() + "_" + fileName;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("파일 경로 생성 실패 - dirName: {}, fileName: {}, error: {}",
                    dirName, fileName, e.getMessage(), e);
            throw new BusinessException(
                    ErrorCode.INVALID_FILE_PATH,
                    "파일 경로 생성에 실패했습니다."
            );
        }
    }

    /**
     * Presigned URL 생성
     */
    private String generatePresignedUrl(String fullPath, String contentType) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fullPath)
                    .contentType(contentType)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(5))
                    .putObjectRequest(putObjectRequest)
                    .build();

            return s3Presigner.presignPutObject(presignRequest).url().toString();

        } catch (Exception e) {
            log.error("Presigned URL 생성 실패 - bucket: {}, key: {}, error: {}",
                    bucket, fullPath, e.getMessage(), e);
            throw new BusinessException(
                    ErrorCode.S3_PRESIGNED_URL_GENERATION_FAILED,
                    "Presigned URL 생성에 실패했습니다."
            );
        }
    }

    /**
     * 파일 URL 생성
     */
    private String generateFileUrl(String fullPath) {
        try {
            return s3Client.utilities()
                    .getUrl(builder -> builder.bucket(bucket).key(fullPath))
                    .toString();
        } catch (Exception e) {
            log.error("파일 URL 생성 실패 - bucket: {}, key: {}, error: {}",
                    bucket, fullPath, e.getMessage(), e);
            throw new BusinessException(
                    ErrorCode.S3_FILE_URL_GENERATION_FAILED,
                    "파일 URL 생성에 실패했습니다."
            );
        }
    }
}
