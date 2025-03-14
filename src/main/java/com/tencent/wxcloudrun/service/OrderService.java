package com.tencent.wxcloudrun.service;


import com.tencent.wxcloudrun.entity.dto.OrderInfo;
import com.tencent.wxcloudrun.entity.request.OrderReq;
import com.tencent.wxcloudrun.entity.request.UserOrderReq;
import com.tencent.wxcloudrun.entity.vo.OrderDetailVo;
import com.tencent.wxcloudrun.entity.vo.PreOrderVo;
import com.tencent.wxcloudrun.exception.ServiceException;

import java.util.List;

/**
 * @author yozora
 * description
 * @version 1.0
 * @date 2025/03/10 0:26
 */
public interface OrderService {

    OrderInfo selectOrderInfoByType(OrderReq orderReq);

    int insertOrderInfo(OrderInfo build);

    String confirmApply(OrderReq orderReq) throws ServiceException;

    String confirmOrder(UserOrderReq orderReq) throws ServiceException;

    String intentOrder(UserOrderReq orderReq) throws ServiceException;

    String updateOrder(OrderReq orderReq) throws ServiceException;

    List<PreOrderVo> list(int page, int size);

    List<OrderDetailVo> userOrders(int page, int size) throws ServiceException;

    List<OrderDetailVo> allUserOrders(int page, int size);

    List<OrderDetailVo> userApples(int type, int page, int size);
}
