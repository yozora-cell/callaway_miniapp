package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.entity.base.ApiResponse;
import com.tencent.wxcloudrun.entity.constant.ReturnConstant;
import com.tencent.wxcloudrun.entity.request.OrderReq;
import com.tencent.wxcloudrun.entity.request.UserOrderReq;
import com.tencent.wxcloudrun.entity.vo.OrderDetailVo;
import com.tencent.wxcloudrun.exception.ServiceException;
import com.tencent.wxcloudrun.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yozora
 * description
 * @version 1.0
 * @date 2025/03/10 11:09
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final OrderService orderService;

    public AdminController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * description: 用户申请
     *
     * @param type 0-会员申请 1-意图订单 2-有效订单
     * @param page page
     * @param size size
     * @return com.tencent.wxcloudrun.entity.base.ApiResponse<java.util.List < com.tencent.wxcloudrun.entity.vo.OrderDetailVo>>
     * @author yozora
     * @date 12:17 2025/3/11
     **/
    @GetMapping("/userApples/{type}/{page}/{size}")
    public ApiResponse<List<OrderDetailVo>> userApples(@PathVariable int type, @PathVariable int page, @PathVariable int size) throws ServiceException {
        return new ApiResponse<>(orderService.userApples(type, page, size));
    }

    /**
     * description: 所有用户订单
     *
     * @param page page
     * @param size size
     * @return com.tencent.wxcloudrun.entity.base.ApiResponse<java.util.List < com.tencent.wxcloudrun.entity.vo.OrderDetailVo>>
     * @author yozora
     * @date 12:10 2025/3/11
     **/
    @GetMapping("/allUserOrders/{page}/{size}")
    public ApiResponse<List<OrderDetailVo>> allUserOrders(@PathVariable int page, @PathVariable int size) throws ServiceException {
        return new ApiResponse<>(orderService.allUserOrders(page, size));
    }

    /**
     * description: 确认申请
     *
     * @param orderReq order request
     * @return com.tencent.wxcloudrun.entity.base.ApiResponse<java.lang.String>
     * @author yozora
     * @date 12:11 2025/3/10
     **/
    @PostMapping("/confirmApply")
    public ApiResponse<String> confirmApply(@RequestBody OrderReq orderReq) throws ServiceException {
        return new ApiResponse<>(orderService.confirmApply(orderReq));
    }

    /**
     * description: 确认订单
     *
     * @param orderReq order request
     * @return com.tencent.wxcloudrun.entity.base.ApiResponse<java.lang.String>
     * @author yozora
     * @date 16:40 2025/3/10
     **/
    @PostMapping("/confirmOrder")
    public ApiResponse<String> confirmOrder(@RequestBody UserOrderReq orderReq) throws ServiceException {
        if (orderReq.getOrderId() == null) {
            throw new ServiceException(ReturnConstant.ORDER_NULL, HttpStatus.BAD_REQUEST.value());
        }
        if (orderReq.getValue() == null) {
            throw new ServiceException(ReturnConstant.ORDER_COUNT_NULL, HttpStatus.BAD_REQUEST.value());
        }
        return new ApiResponse<>(orderService.confirmOrder(orderReq));
    }

    /**
     * description: 快速确认订单
     *
     * @param orderReq order request
     * @return com.tencent.wxcloudrun.entity.base.ApiResponse<java.lang.String>
     * @author yozora
     * @date 18:29 2025/3/15
     **/
    @PostMapping("/fastConfirmOrder")
    public ApiResponse<String> fastConfirmOrder(@RequestBody UserOrderReq orderReq) throws ServiceException {
        if (orderReq.getPreOrderId() == null) {
            throw new ServiceException(ReturnConstant.PRE_ORDER_NULL, HttpStatus.BAD_REQUEST.value());
        }
        if (orderReq.getUserId() == null) {
            throw new ServiceException(ReturnConstant.USER_NULL, HttpStatus.BAD_REQUEST.value());
        }
        if (orderReq.getValue() == null) {
            throw new ServiceException(ReturnConstant.ORDER_COUNT_NULL, HttpStatus.BAD_REQUEST.value());
        }
        return new ApiResponse<>(orderService.fastConfirmOrder(orderReq));
    }


    /**
     * description: 更新订单
     *
     * @param orderReq order request
     * @return com.tencent.wxcloudrun.entity.base.ApiResponse<java.lang.String>
     * @author yozora
     * @date 16:46 2025/3/10
     **/
    @PutMapping("/updateOrder")
    public ApiResponse<String> updateOrder(@RequestBody OrderReq orderReq) throws ServiceException {
        if (orderReq.getOrderId() == null) {
            throw new ServiceException(ReturnConstant.ORDER_NULL, HttpStatus.BAD_REQUEST.value());
        }
        if (orderReq.getValue() == null) {
            throw new ServiceException(ReturnConstant.ORDER_COUNT_NULL, HttpStatus.BAD_REQUEST.value());
        }
        return new ApiResponse<>(orderService.updateOrder(orderReq));
    }

}
