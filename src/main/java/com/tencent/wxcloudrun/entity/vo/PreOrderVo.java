package com.tencent.wxcloudrun.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author yozora
 * description 套餐信息
 * @version 1.0
 * @date 2025/03/11 11:54
 */
@Data
public class PreOrderVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String coach;

    private BigDecimal price;

    private Integer value;

    /**
     * 订单内容
     */
    private String content;
}
