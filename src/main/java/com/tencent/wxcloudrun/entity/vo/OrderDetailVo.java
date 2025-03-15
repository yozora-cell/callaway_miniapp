package com.tencent.wxcloudrun.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author yozora
 * description
 * @version 1.0
 * @date 2025/03/11 12:03
 */
@Data
public class OrderDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

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
}
