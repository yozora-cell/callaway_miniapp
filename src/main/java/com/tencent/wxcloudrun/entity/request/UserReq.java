package com.tencent.wxcloudrun.entity.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author yozora
 * description 用户请求
 * @version 1.0
 * @date 2025/03/10 12:13
 */
@Data
public class UserReq {

    @NotBlank(message = "name can not be null")
    private String name;

    @NotBlank(message = "phone can not be null")
    private String phone;
}
