package com.starterpack.exception;

public class KakaoAuthException extends BusinessException {
    public KakaoAuthException() {
        super(ErrorCode.KAKAO_AUTH_ERROR);
    }

    public KakaoAuthException(String detailMessage) {
        super(ErrorCode.KAKAO_AUTH_ERROR, detailMessage);
    }
}
