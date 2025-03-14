package com.tencent.wxcloudrun.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * order_detail_info
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailInfo implements Serializable {

    private Long id;

    private Long userId;

    private Long orderId;

    /**
     * 订单模板
     */
    private Integer preOrderId;

    private String name;

    private String phone;

    private String coach;

    /**
     * 次数
     */
    private Integer value;

    private BigDecimal price;

    /**
     * 订单内容
     */
    private String content;

    private String isDel;

    private Date createTime;

    private Date updateTime;

    @Serial
    private static final long serialVersionUID = 1L;
}