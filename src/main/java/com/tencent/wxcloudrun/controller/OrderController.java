package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.entity.base.ApiResponse;
import com.tencent.wxcloudrun.entity.constant.ReturnConstant;
import com.tencent.wxcloudrun.entity.request.UserOrderReq;
import com.tencent.wxcloudrun.entity.vo.OrderDetailVo;
import com.tencent.wxcloudrun.entity.vo.PreOrderVo;
import com.tencent.wxcloudrun.exception.ServiceException;
import com.tencent.wxcloudrun.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yozora
 * description
 * @version 1.0
 * @date 2025/03/10 0:23
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * description: 用户订单
     *
     * @param page page
     * @param size size
     * @return com.tencent.wxcloudrun.entity.base.ApiResponse<java.util.List < com.tencent.wxcloudrun.entity.vo.OrderDetailVo>>
     * @author yozora
     * @date 12:10 2025/3/11
     **/
    @GetMapping("/userOrders/{page}/{size}")
    public ApiResponse<List<OrderDetailVo>> userOrders(@PathVariable int page, @PathVariable int size) throws ServiceException {
        return new ApiResponse<>(orderService.userOrders(page, size));
    }

    /**
     * description: 套餐列表
     *
     * @param page page
     * @param size size
     * @return com.tencent.wxcloudrun.entity.base.ApiResponse<java.util.List < com.tencent.wxcloudrun.entity.vo.PreOrderVo>>
     * @author yozora
     * @date 12:00 2025/3/11
     **/
    @GetMapping("/packages/{page}/{size}")
    public ApiResponse<List<PreOrderVo>> list(@PathVariable int page, @PathVariable int size) {
        return new ApiResponse<>(orderService.list(page, size));
    }

    /**
     * description: 意图订单
     *
     * @param orderReq order request
     * @return com.tencent.wxcloudrun.entity.base.ApiResponse<java.lang.String>
     * @author yozora
     * @date 16:01 2025/3/10
     **/
    @PostMapping("/intentOrder")
    public ApiResponse<String> intentOrder(@RequestBody UserOrderReq orderReq) throws ServiceException {
        if (orderReq.getPreOrderId() == null) {
            throw new ServiceException(ReturnConstant.PRE_ORDER_NULL, HttpStatus.BAD_REQUEST.value());
        }
        return new ApiResponse<>(orderService.intentOrder(orderReq));
    }
}
