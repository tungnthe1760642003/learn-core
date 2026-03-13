package org.example.learncore.exception;

import lombok.Getter;
import org.example.learncore.dto.ErrorCode;

@Getter
public class AppException extends RuntimeException {

    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
