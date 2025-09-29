package com.starterpack.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String errorCode,
        String errorMessage,
        List<FieldErrorDetail> errors
) {
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.getErrorCode(),
                errorCode.getErrorMessage(),
                null
        );
    }

    public static ErrorResponse of(ErrorCode errorCode, List<FieldErrorDetail> errors) {
        return new ErrorResponse(
                errorCode.getErrorCode(),
                errorCode.getErrorMessage(),
                errors
        );
    }
}
