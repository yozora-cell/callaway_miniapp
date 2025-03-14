package com.tencent.wxcloudrun.entity.base;

import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;

/**
 * Created by yozora
 * Date: 2018/5/23
 */
public class ApiResponse<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 8732277221304105593L;

    /**
     * 默认返回信息
     */
    private String msg;

    /**
     * 默认返回成功
     */
    private Integer code;

    /**
     * 返回数据
     */
    private T data;


    public ApiResponse() {
        this.code = HttpStatus.OK.value();
        this.msg = "success";
    }

    /**
     * 自定义
     *
     * @param msg 返回信息
     */
    public ApiResponse(Integer code, String msg) {
        this(code, msg, null);
    }

    public ApiResponse(T data) {
        this.code = HttpStatus.OK.value();
        this.data = data;
        this.msg = "success";
    }

    public ApiResponse(Integer code, String msg, T data) {
        this.msg = msg;
        this.code = code;
        if (data != null) this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultDTO [msg=" + msg + ", code=" + code + ", data=" + data + "]";
    }
}
