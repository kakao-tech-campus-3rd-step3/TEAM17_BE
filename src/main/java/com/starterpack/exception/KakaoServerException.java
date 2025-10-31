package com.starterpack.exception;

public class KakaoServerException extends BusinessException {
    public KakaoServerException() {
        super(ErrorCode.KAKAO_SERVER_ERROR);
    }

    public KakaoServerException(String detailMessage) {
        super(ErrorCode.KAKAO_SERVER_ERROR, detailMessage);
    }
}
