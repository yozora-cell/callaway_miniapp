package com.tencent.wxcloudrun.entity.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * pre_order_info
 */
@Data
public class PreOrderInfo implements Serializable {

    private Integer id;

    private String coach;

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