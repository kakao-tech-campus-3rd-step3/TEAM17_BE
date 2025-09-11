package com.starterpack.exception;

public record ErrorResponse(String errorCode, String errorMessage) {

    public static ErrorResponse of(final ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getErrorCode(),
                errorCode.getErrorMessage());
    }
}
