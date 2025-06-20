package com.toy.toyback.exceptions;

import com.toy.toyback.code.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getTitle());
        this.errorCode = errorCode;
    }
}
