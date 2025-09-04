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
            "해당하는 팩을 찾을 수 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;
}
