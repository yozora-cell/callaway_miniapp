package com.tencent.wxcloudrun.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author yozora
 * description
 * @version 1.0
 * @date 2025/03/09 20:45
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {

    private Long id;

    private String openId;

    private String name;

    private String phone;

    private String adminStatus;

    private String vipApplyStatus;

    private String vipStatus;

    /**
     * 0-否 1是
     */
    private String isDel;

    private Date createTime;

    private Date updateTime;
}
