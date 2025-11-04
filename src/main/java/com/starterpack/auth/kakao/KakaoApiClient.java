package com.starterpack.auth.kakao;

import com.starterpack.auth.dto.KakaoTokenResponseDto;
import com.starterpack.auth.dto.KakaoUserInfoResponseDto;
import com.starterpack.exception.KakaoAuthException;
import com.starterpack.exception.KakaoServerException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class KakaoApiClient {

    private static final int CONNECTION_TIMEOUT_MS = 5000;  // 5초
    private static final int READ_TIMEOUT_MS = 10000;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;
    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String tokenUri;
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String userInfoUri;

    private final RestClient restClient;

    public KakaoApiClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(CONNECTION_TIMEOUT_MS));
        requestFactory.setReadTimeout(Duration.ofMillis(READ_TIMEOUT_MS));

        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    /**
     * 인가 코드를 사용하여 카카오로부터 액세스 토큰을 받아옵니다.
     */
    public KakaoTokenResponseDto fetchAccessToken(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("redirect_uri", redirectUri);
        formData.add("code", code);

        try {
            return restClient.post()
                    .uri(tokenUri)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        throw new KakaoAuthException(
                                "카카오 토큰 발급 실패 - 응답 코드: " + response.getStatusCode()
                        );
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new KakaoServerException(
                                "카카오 서버 오류 - 응답 코드: " + response.getStatusCode()
                        );
                    })
                    .body(KakaoTokenResponseDto.class);
        } catch (ResourceAccessException e) {
            throw new KakaoServerException("카카오 서버 연결 실패 (타임아웃 또는 네트워크 오류)");
        }
    }

    /**
     * 액세스 토큰을 사용하여 카카오 API로부터 사용자 정보를 받아옵니다.
     */
    public KakaoUserInfoResponseDto fetchUserInfo(String accessToken) {
        try {
            return restClient.get()
                    .uri(userInfoUri)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        throw new KakaoAuthException(
                                "카카오 사용자 정보 조회 실패 - 응답 코드: " + response.getStatusCode()
                        );
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new KakaoServerException(
                                "카카오 서버 오류 - 응답 코드: " + response.getStatusCode()
                        );
                    })
                    .body(KakaoUserInfoResponseDto.class);
        } catch (ResourceAccessException e) {
            throw new KakaoServerException("카카오 서버 연결 실패 (타임아웃 또는 네트워크 오류)");
        }
    }
}
