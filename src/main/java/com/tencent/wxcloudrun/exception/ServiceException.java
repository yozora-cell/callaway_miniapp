package com.tencent.wxcloudrun.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class ServiceException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Integer code;

    public ServiceException() {
        this(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
    }

    public ServiceException(String msg, Integer code) {
        this(msg, code, null);
    }

    public ServiceException(String msg, Integer code, Exception nestedEx) {
        super(msg, nestedEx);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

}