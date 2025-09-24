package com.starterpack.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    //Global
    BAD_REQUEST(HttpStatus.BAD_REQUEST,
            "G001",
            "유효하지 않은 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "G002",
            "서버 내부 오류가 발생했습니다."),
    //Product
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND,
            "P001",
            "해당하는 상품을 찾을 수 없습니다."),
    //Category
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND,
            "C001",
            "해당하는 카테고리를 찾을 수 없습니다."),
    //Pack
    PACK_NOT_FOUND(HttpStatus.NOT_FOUND,
            "PK001",
            "해당하는 팩을 찾을 수 없습니다."),
    //Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND,
            "M001",
            "해당하는 멤버를 찾을 수 없습니다."),
    MEMBER_EMAIL_DUPLICATED(HttpStatus.CONFLICT,
            "M002",
            "이미 사용 중인 이메일입니다."),
    MEMBER_PROVIDER_ID_DUPLICATED(HttpStatus.CONFLICT,
            "M003",
            "이미 사용 중인 소셜 로그인 계정입니다."),
    //Security
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED,
            "S001",
            "유효하지 않은 토큰입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN,
            "S002",
            "요청에 대한 접근 권한이 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,
            "S003",
            "인증이 필요합니다."),
    INVALID_AUTH_PRINCIPAL(HttpStatus.UNAUTHORIZED,
            "S004",
            "인증 정보가 올바르지 않습니다."),
    //Login
    INVALID_LOGIN_PROVIDER(HttpStatus.BAD_REQUEST,
            "L001",
            "이메일 회원가입 유저가 아닙니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED,
            "L002",
            "비밀번호가 틀렸습니다."),
    //URL/Link Validation
    URL_INVALID_FORMAT(HttpStatus.BAD_REQUEST,
            "U001",
            "유효하지 않은 URL 형식입니다."),
    URL_FORBIDDEN_SCHEME(HttpStatus.BAD_REQUEST,
            "U002",
            "허용되지 않은 URL 스킴입니다."),
    URL_SHORTENER_BLOCKED(HttpStatus.BAD_REQUEST,
            "U003",
            "단축 링크 도메인은 허용되지 않습니다."),
    //feed
    FEED_NOT_FOUND(HttpStatus.NOT_FOUND,
            "F001",
            "해당하는 피드를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;
}
