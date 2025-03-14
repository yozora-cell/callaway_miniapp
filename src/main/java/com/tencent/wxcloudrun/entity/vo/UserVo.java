package com.tencent.wxcloudrun.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yozora
 * description 用户视图对象
 * @version 1.0
 * @date 2025/03/11 11:43
 */
@Data
public class UserVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String phone;

    private String adminStatus;

    private String vipApplyStatus;

    private String vipStatus;
}
