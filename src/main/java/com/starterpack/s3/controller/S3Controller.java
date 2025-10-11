package com.starterpack.s3.controller;

import com.starterpack.s3.service.S3Service;
import com.starterpack.s3.dto.PresignedUrlsRequestDto;
import com.starterpack.s3.dto.PresignedUrlsResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
@Tag(name = "S3 File Upload", description = "S3 파일 업로드 관련 API")
public class S3Controller {

    private final S3Service s3Service;

    /**
     * 파일 업로드를 위한 Presigned URL을 생성하여 반환합니다.
     * @param requestDto 업로드할 파일의 이름
     * @param dirName    파일을 저장할 S3 내의 디렉토리 이름 (예: "feeds", "profiles")
     * @return Presigned URL과 최종 파일 URL
     */
    @PostMapping("/presigned-urls")
    @Operation(summary = "Presigned URL 다중 생성", description = "여러 파일 업로드를 위한 임시 URL 목록을 발급합니다")
    @SecurityRequirement(name = "CookieAuthentication")
    public ResponseEntity<List<PresignedUrlsResponseDto>> getPresignedUrls(
            @RequestBody PresignedUrlsRequestDto requestDto,
            @RequestParam String dirName
    ) {
        List<PresignedUrlsResponseDto> responseDto = s3Service.generatePresignedUrls(dirName, requestDto);

        return ResponseEntity.ok(responseDto);
    }
}
