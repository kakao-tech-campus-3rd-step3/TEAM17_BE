package com.starterpack.linkpolicy.service;

import com.starterpack.exception.BusinessException;
import com.starterpack.exception.ErrorCode;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class LinkModerationService {

    private final LinkPolicyService linkPolicyService;

    public LinkModerationService(LinkPolicyService linkPolicyService) {
        this.linkPolicyService = linkPolicyService;
    }

    // 허용 스킴: http, https (더 유연한 패턴)
    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(?i)(https?)://[A-Za-z0-9.-]+(?::\\d+)?(?:/.*)?$"
    );

    // 기본 단축링크 차단 호스트 목록
    private static final Set<String> SHORTENER_BLOCKLIST = Set.of(
            "bit.ly", "t.co", "tinyurl.com", "goo.gl", "ow.ly", "is.gd", "rebrand.ly", "buff.ly",
            "lnkd.in", "cutt.ly", "soo.gd", "v.gd", "shrtco.de", "shorturl.at"
    );

    // URL 내 HTML/스크립트 관련 위험 문자 제거 - Apache Commons Text 사용
    public String sanitizeHtmlFromUrl(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        
        // Apache Commons Text를 사용하여 HTML 엔티티 디코딩 및 태그 제거
        String sanitized = StringEscapeUtils.unescapeHtml4(url)
                .replaceAll("<[^>]*>", ""); // HTML 태그 제거
        
        // 추가 위험 문자 제거
        sanitized = sanitized
                .replace("\\", "")
                .replace("\"", "")
                .replace("'", "");
        
        // javascript:, data: 스킴 방지 (대소문자 무시)
        String lower = sanitized.toLowerCase();
        if (lower.startsWith("javascript:") || lower.startsWith("data:")) {
            throw new BusinessException(ErrorCode.URL_FORBIDDEN_SCHEME);
        }
        return sanitized.trim();
    }

    // 정규식으로 URL 형식 검증
    public boolean validateUrlByRegex(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        return URL_PATTERN.matcher(url.trim()).matches();
    }

    // 단축링크 호스트 차단
    public boolean isShortenedUrlBlocked(String url) {
        String host = extractHost(url);
        if (host == null) {
            return true; // 호스트를 파싱하지 못하면 차단
        }
        String normalized = host.toLowerCase();
        return SHORTENER_BLOCKLIST.contains(normalized);
    }

    // DB 블랙리스트 검사
    public boolean isUrlBlockedByBlacklist(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        
        List<String> blacklistPatterns = linkPolicyService.getAll().stream()
                .map(dto -> dto.pattern())
                .toList();
        
        String normalizedUrl = url.toLowerCase().trim();
        
        for (String pattern : blacklistPatterns) {
            if (pattern != null && !pattern.isBlank() && normalizedUrl.contains(pattern.toLowerCase())) {
                return true;
            }
        }
        
        return false;
    }

    // 종합 검사: 안전하지 않으면 예외를 던짐
    public void assertSafeProductLink(String url) {
        String sanitized = sanitizeHtmlFromUrl(url);
        if (!validateUrlByRegex(sanitized)) {
            throw new BusinessException(ErrorCode.URL_INVALID_FORMAT);
        }
        if (isShortenedUrlBlocked(sanitized)) {
            throw new BusinessException(ErrorCode.URL_SHORTENER_BLOCKED);
        }
        if (isUrlBlockedByBlacklist(sanitized)) {
            throw new BusinessException(ErrorCode.URL_BLACKLIST_BLOCKED);
        }
    }

    // 종합 검사 + sanitized URL 반환
    public String validateAndSanitizeProductLink(String url) {
        String sanitized = sanitizeHtmlFromUrl(url);
        if (!validateUrlByRegex(sanitized)) {
            throw new BusinessException(ErrorCode.URL_INVALID_FORMAT);
        }
        if (isShortenedUrlBlocked(sanitized)) {
            throw new BusinessException(ErrorCode.URL_SHORTENER_BLOCKED);
        }
        if (isUrlBlockedByBlacklist(sanitized)) {
            throw new BusinessException(ErrorCode.URL_BLACKLIST_BLOCKED);
        }
        return sanitized;
    }

    private String extractHost(String url) {
        if (url == null || url.isBlank()) return null;
        try {
            URI uri = new URI(url.trim());
            return uri.getHost();
        } catch (URISyntaxException e) {
            return null;
        }
    }
}


