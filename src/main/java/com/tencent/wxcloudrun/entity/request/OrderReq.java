package com.tencent.wxcloudrun.entity.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author yozora
 * description 订单请求
 * @version 1.0
 * @date 2025/03/10 0:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderReq {

    @NotNull(message = "user id can not be null")
    private Long userId;

    private Long orderId;

    private Integer value;

    private Integer preOrderId;

    private String orderType;

    private String isActive;
}
