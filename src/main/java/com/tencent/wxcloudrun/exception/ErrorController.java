package com.tencent.wxcloudrun.exception;

import lombok.extern.log4j.Log4j2;
import com.tencent.wxcloudrun.entity.base.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 功能：全局异常处理
 *
 * @author yozora
 */
@Log4j2
@RestControllerAdvice(basePackages = "org.cell.slg.controller")
public class ErrorController {


    @Autowired
    protected HttpServletRequest request;

    @ExceptionHandler(ServiceException.class)
    public ApiResponse<Serializable> handleServiceException(ServiceException e) {
        log.error("service error", e);
        log.error(request.getRequestURL().toString());
        return new ApiResponse<>(e.getCode(), e.getMessage());
    }

    /**
     * 401 - unauthorized
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public ApiResponse<Serializable> handleAuthenticationException(AuthenticationException e) {
        log.error("invalid token", e);
        log.error(request.getRequestURL().toString());
        return new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }


    /**
     * 405 - Method Not Allowed
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResponse<Serializable> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("unsupported method", e);
        log.error(request.getRequestURL().toString());
        return new ApiResponse<>(HttpStatus.METHOD_NOT_ALLOWED.value(), HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase());
    }

    /**
     * mybatis错误
     *
     * @param ex DataAccessException
     * @return Response
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DataAccessException.class)
    public ApiResponse<Serializable> dataAccessErrorHandler(DataAccessException ex) {
        ex.printStackTrace();
        log.error(request.getRequestURL().toString());
        return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Map<String, String>> handleValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        Map<String, String> errorMsg = e.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (k1, k2) -> k1));
        return new ApiResponse<>(500, "Parameter error", errorMsg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<String> handleValidationException(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        String errorMsg = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(","));
        return new ApiResponse<>(500, "Parameter error", errorMsg);
    }

    @ExceptionHandler(BindException.class)
    public ApiResponse<Map<String, String>> handleBindException(BindException e) {
        log.error(e.getMessage(), e);
        Map<String, String> errorMsg = e.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (k1, k2) -> k1));
        return new ApiResponse<>(500, "Parameter error", errorMsg);
    }

    /**
     * 处理所有controller 中的异常信息
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Serializable> exceptionHandler(Exception ex) {
        //获取详细堆栈信息
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        log.error(sw.toString());
        log.error(request.getRequestURL().toString());
        return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

}
