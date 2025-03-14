package com.tencent.wxcloudrun.entity.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author yozora
 * description
 * @version 1.0
 */
@Data
public class WeChatLoginReq {

    @NotBlank(message = "code can not be blank")
    private String code;

}
