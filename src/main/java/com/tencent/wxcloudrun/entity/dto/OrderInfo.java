package com.tencent.wxcloudrun.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * order_info
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderInfo implements Serializable {

    private Long id;

    private Long userId;

    private Long adminId;

    /**
     * 0-会员申请 1-购买意向订单 2-有效订单
     */
    private String orderType;

    /**
     * 订单模板
     */
    private Integer preOrderId;

    private String isActive;

    private String isDel;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}