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
            "이미 사용 중인 소셜 로그인 계정입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;
}
