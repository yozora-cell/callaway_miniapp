package com.tencent.wxcloudrun.entity.request;

import lombok.Data;

/**
 * @author yozora
 * description
 * @version 1.0
 * @date 2025/03/10 16:30
 */
@Data
public class UserOrderReq {

    private Long userId;

    private Long orderId;

    private Integer value;

    private Integer preOrderId;

}
