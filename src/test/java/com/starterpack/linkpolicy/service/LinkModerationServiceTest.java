package com.starterpack.linkpolicy.service;

import com.starterpack.exception.BusinessException;
import com.starterpack.exception.ErrorCode;
import com.starterpack.linkpolicy.dto.LinkPolicyResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinkModerationServiceTest {

    @Mock
    private LinkPolicyService linkPolicyService;

    @InjectMocks
    private LinkModerationService linkModerationService;

    @BeforeEach
    void setUp() {
        // 기본 블랙리스트 패턴 설정 (lenient로 설정하여 불필요한 stubbing 경고 방지)
        lenient().when(linkPolicyService.getAll()).thenReturn(List.of(
                new LinkPolicyResponseDto(1L, "malicious.com", "악성 사이트", LocalDateTime.now()),
                new LinkPolicyResponseDto(2L, "evil.org", "피싱 사이트", LocalDateTime.now()),
                new LinkPolicyResponseDto(3L, "spam.net", "스팸 사이트", LocalDateTime.now())
        ));
    }

    @Test
    @DisplayName("HTML 태그 제거 테스트")
    void sanitizeHtmlFromUrl() {
        // Given
        String urlWithHtml = "https://example.com<script>alert('xss')</script>";
        String expected = "https://example.comalert(xss)";

        // When
        String result = linkModerationService.sanitizeHtmlFromUrl(urlWithHtml);

        // Then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("HTML 태그 제거 테스트 - 복잡한 태그")
    void sanitizeHtmlFromUrl_ComplexTags() {
        // Given
        String urlWithComplexHtml = "https://example.com<div><script>alert('xss')</script><img src='x'></div>";
        String expected = "https://example.comalert(xss)";

        // When
        String result = linkModerationService.sanitizeHtmlFromUrl(urlWithComplexHtml);

        // Then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("정상 URL 형식 검증 테스트")
    void validateUrlByRegex_ValidUrl() {
        // Given
        String validUrl = "https://www.naver.com";

        // When
        boolean result = linkModerationService.validateUrlByRegex(validUrl);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("잘못된 URL 형식 검증 테스트")
    void validateUrlByRegex_InvalidUrl() {
        // Given
        String invalidUrl = "ftp://example.com";

        // When
        boolean result = linkModerationService.validateUrlByRegex(invalidUrl);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("스킴 누락 URL 검증 테스트")
    void validateUrlByRegex_MissingScheme() {
        // Given
        String urlWithoutScheme = "example.com";

        // When
        boolean result = linkModerationService.validateUrlByRegex(urlWithoutScheme);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("단축링크 차단 테스트")
    void isShortenedUrlBlocked() {
        // Given
        String shortenerUrl = "https://bit.ly/abc123";

        // When
        boolean result = linkModerationService.isShortenedUrlBlocked(shortenerUrl);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("정상 도메인 단축링크 테스트")
    void isShortenedUrlBlocked_NormalDomain() {
        // Given
        String normalUrl = "https://www.google.com";

        // When
        boolean result = linkModerationService.isShortenedUrlBlocked(normalUrl);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("DB 블랙리스트 차단 테스트")
    void isUrlBlockedByBlacklist() {
        // Given
        String blockedUrl = "https://malicious.com/evil";

        // When
        boolean result = linkModerationService.isUrlBlockedByBlacklist(blockedUrl);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("DB 블랙리스트 허용 테스트")
    void isUrlBlockedByBlacklist_Allowed() {
        // Given
        String allowedUrl = "https://www.safe.com";

        // When
        boolean result = linkModerationService.isUrlBlockedByBlacklist(allowedUrl);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("종합 검사 - 정상 링크")
    void assertSafeProductLink_ValidLink() {
        // Given
        String validUrl = "https://www.naver.com";

        // When & Then
        assertThatCode(() -> linkModerationService.assertSafeProductLink(validUrl))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("종합 검사 - HTML 태그 포함")
    void assertSafeProductLink_HtmlTag() {
        // Given
        String urlWithHtml = "https://example.com<script>alert('xss')</script>";

        // When & Then
        assertThatThrownBy(() -> linkModerationService.assertSafeProductLink(urlWithHtml))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.URL_INVALID_FORMAT);
    }

    @Test
    @DisplayName("종합 검사 - HTML 태그 포함 후 잘못된 형식")
    void assertSafeProductLink_HtmlTagInvalidFormat() {
        // Given
        String urlWithHtml = "example.com<script>alert('xss')</script>"; // 스킴 없음

        // When & Then
        assertThatThrownBy(() -> linkModerationService.assertSafeProductLink(urlWithHtml))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.URL_INVALID_FORMAT);
    }

    @Test
    @DisplayName("종합 검사 - 잘못된 URL 형식")
    void assertSafeProductLink_InvalidFormat() {
        // Given
        String invalidUrl = "ftp://example.com";

        // When & Then
        assertThatThrownBy(() -> linkModerationService.assertSafeProductLink(invalidUrl))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.URL_INVALID_FORMAT);
    }

    @Test
    @DisplayName("종합 검사 - 단축링크 차단")
    void assertSafeProductLink_ShortenerBlocked() {
        // Given
        String shortenerUrl = "https://bit.ly/abc123";

        // When & Then
        assertThatThrownBy(() -> linkModerationService.assertSafeProductLink(shortenerUrl))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.URL_SHORTENER_BLOCKED);
    }

    @Test
    @DisplayName("종합 검사 - DB 블랙리스트 차단")
    void assertSafeProductLink_BlacklistBlocked() {
        // Given
        String blockedUrl = "https://malicious.com/evil";

        // When & Then
        assertThatThrownBy(() -> linkModerationService.assertSafeProductLink(blockedUrl))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.URL_SHORTENER_BLOCKED);
    }

    @Test
    @DisplayName("null URL 처리 테스트")
    void handleNullUrl() {
        // Given
        String nullUrl = null;

        // When
        boolean result = linkModerationService.isUrlBlockedByBlacklist(nullUrl);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("빈 문자열 URL 처리 테스트")
    void handleEmptyUrl() {
        // Given
        String emptyUrl = "";

        // When
        boolean result = linkModerationService.isUrlBlockedByBlacklist(emptyUrl);

        // Then
        assertThat(result).isFalse();
    }
}
