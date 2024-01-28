package com.campfood.common.exception;

import com.campfood.common.error.ErrorCode;

public class AlreadyExistMemberException extends RuntimeException{
    private final ErrorCode errorCode;

    public AlreadyExistMemberException(String message, ErrorCode errorCode){
        super(message);
        this.errorCode = errorCode;
    }
}