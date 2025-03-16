package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.OrderDetailInfoMapper;
import com.tencent.wxcloudrun.dao.OrderInfoMapper;
import com.tencent.wxcloudrun.dao.PreOrderInfoMapper;
import com.tencent.wxcloudrun.dao.UserMapper;
import com.tencent.wxcloudrun.entity.base.PageInfo;
import com.tencent.wxcloudrun.entity.constant.BaseConstant;
import com.tencent.wxcloudrun.entity.constant.OrderConstant;
import com.tencent.wxcloudrun.entity.constant.ReturnConstant;
import com.tencent.wxcloudrun.entity.dto.OrderDetailInfo;
import com.tencent.wxcloudrun.entity.dto.OrderInfo;
import com.tencent.wxcloudrun.entity.dto.PreOrderInfo;
import com.tencent.wxcloudrun.entity.dto.UserInfo;
import com.tencent.wxcloudrun.entity.request.OrderReq;
import com.tencent.wxcloudrun.entity.request.UserOrderReq;
import com.tencent.wxcloudrun.entity.vo.OrderDetailVo;
import com.tencent.wxcloudrun.entity.vo.PreOrderVo;
import com.tencent.wxcloudrun.exception.ServiceException;
import com.tencent.wxcloudrun.service.OrderService;
import com.tencent.wxcloudrun.utils.JWTUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yozora
 * description order service
 * @version 1.0
 * @date 2025/03/10 0:26
 */
@Service
public class OrderServiceImpl implements OrderService {


    private final OrderInfoMapper orderInfoMapper;

    private final UserMapper userMapper;
    private final PreOrderInfoMapper preOrderInfoMapper;
    private final OrderDetailInfoMapper orderDetailInfoMapper;

    public OrderServiceImpl(OrderInfoMapper orderInfoMapper, UserMapper userMapper, PreOrderInfoMapper preOrderInfoMapper, OrderDetailInfoMapper orderDetailInfoMapper) {
        this.orderInfoMapper = orderInfoMapper;
        this.userMapper = userMapper;
        this.preOrderInfoMapper = preOrderInfoMapper;
        this.orderDetailInfoMapper = orderDetailInfoMapper;
    }

    @Override
    public OrderInfo selectOrderInfoByType(OrderReq orderReq) {
        return orderInfoMapper.selectOrderInfoByType(orderReq);
    }

    @Override
    public int insertOrderInfo(OrderInfo build) {
        return orderInfoMapper.insertSelective(build);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String confirmApply(OrderReq orderReq) throws ServiceException {

        isAdmin();
        UserInfo userInfo = userMapper.selectByPrimaryKey(orderReq.getUserId());
        if (userInfo == null) {
            throw new ServiceException(ReturnConstant.NO_USER_INFO, HttpStatus.BAD_REQUEST.value());
        }
        orderReq.setOrderType(OrderConstant.APPLY_ORDER);
        orderReq.setIsActive(BaseConstant.NO);
        OrderInfo orderInfo = orderInfoMapper.selectOrderInfoByType(orderReq);
        if (orderInfo == null) {
            throw new ServiceException(ReturnConstant.NO_APPLY_INFO, HttpStatus.BAD_REQUEST.value());
        }

        UserInfo build1 = UserInfo.builder()
                .id(orderReq.getUserId())
                .vipApplyStatus(BaseConstant.YES)
                .updateTime(new Date())
                .build();
        userMapper.updateByPrimaryKeySelective(build1);
        OrderInfo build = OrderInfo.builder()
                .id(orderInfo.getId())
                .isActive(BaseConstant.YES)
                .updateTime(new Date())
                .build();
        orderInfoMapper.updateByPrimaryKeySelective(build);
        return "success";
    }

    @Override
    public String intentOrder(UserOrderReq orderReq) throws ServiceException {

        UserInfo user = JWTUtils.getUser();
        PreOrderInfo preOrderInfo = preOrderInfoMapper.selectByPrimaryKey(orderReq.getPreOrderId());
        if (preOrderInfo == null) {
            throw new ServiceException(ReturnConstant.NO_PRE_ORDER, HttpStatus.BAD_REQUEST.value());
        }
        OrderInfo build = OrderInfo.builder()
                .userId(user.getId())
                .preOrderId(orderReq.getPreOrderId())
                .orderType(OrderConstant.INTENTION_ORDER)
                .isActive(BaseConstant.NO)
                .isDel(BaseConstant.NO)
                .createTime(new Date())
                .updateTime(new Date())
                .build();
        orderInfoMapper.insertSelective(build);
        return "success";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String confirmOrder(UserOrderReq orderReq) throws ServiceException {

        isAdmin();
        OrderInfo orderInfo = orderInfoMapper.selectByPrimaryKey(orderReq.getOrderId());
        if (orderInfo == null) {
            throw new ServiceException(ReturnConstant.NO_ORDER, HttpStatus.BAD_REQUEST.value());
        }
        PreOrderInfo preOrderInfo = preOrderInfoMapper.selectByPrimaryKey(orderReq.getPreOrderId());
        if (preOrderInfo == null) {
            throw new ServiceException(ReturnConstant.NO_PRE_ORDER, HttpStatus.BAD_REQUEST.value());
        }
        UserInfo userInfo = userMapper.selectByPrimaryKey(orderInfo.getUserId());
        if (userInfo == null) {
            throw new ServiceException(ReturnConstant.NO_USER_INFO, HttpStatus.BAD_REQUEST.value());
        }
        if (userInfo.getVipStatus().equals(BaseConstant.NO)) {
            UserInfo build = UserInfo.builder()
                    .id(userInfo.getId())
                    .vipStatus(BaseConstant.YES)
                    .updateTime(new Date())
                    .build();
            userMapper.updateByPrimaryKeySelective(build);
        }

        OrderDetailInfo orderDetailInfo = orderDetailInfoMapper.selectByUserIdAndPreOrderId(orderInfo.getUserId(), orderReq.getPreOrderId());
        if (orderDetailInfo != null) {
            // 增加订单次数
            OrderDetailInfo build1 = OrderDetailInfo.builder()
                    .id(orderDetailInfo.getId())
                    .value(orderDetailInfo.getValue() + orderReq.getValue())
                    .updateTime(new Date())
                    .build();
            orderDetailInfoMapper.updateByPrimaryKeySelective(build1);
        } else {
            // 创建订单
            OrderDetailInfo build1 = OrderDetailInfo.builder()
                    .userId(orderInfo.getUserId())
                    .orderId(orderReq.getOrderId())
                    .preOrderId(orderReq.getPreOrderId())
                    .name(userInfo.getName())
                    .phone(userInfo.getPhone())
                    .value(orderReq.getValue())
                    .coach(preOrderInfo.getCoach())
                    .price(preOrderInfo.getPrice())
                    .content(preOrderInfo.getContent())
                    .isDel(BaseConstant.NO)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            orderDetailInfoMapper.insertSelective(build1);
        }

        OrderInfo build = OrderInfo.builder()
                .id(orderInfo.getId())
                .isActive(BaseConstant.YES)
                .updateTime(new Date())
                .build();
        orderInfoMapper.updateByPrimaryKeySelective(build);
        return "success";
    }

    @Override
    public String fastConfirmOrder(UserOrderReq orderReq) throws ServiceException {
        isAdmin();
        PreOrderInfo preOrderInfo = preOrderInfoMapper.selectByPrimaryKey(orderReq.getPreOrderId());
        if (preOrderInfo == null) {
            throw new ServiceException(ReturnConstant.NO_PRE_ORDER, HttpStatus.BAD_REQUEST.value());
        }
        UserInfo userInfo = userMapper.selectByPrimaryKey(orderReq.getUserId());
        if (userInfo == null) {
            throw new ServiceException(ReturnConstant.NO_USER_INFO, HttpStatus.BAD_REQUEST.value());
        }
        OrderDetailInfo orderDetailInfo = orderDetailInfoMapper.selectByUserIdAndPreOrderId(orderReq.getUserId(), orderReq.getPreOrderId());
        if (orderDetailInfo != null) {
            // 增加订单次数
            OrderDetailInfo build1 = OrderDetailInfo.builder()
                    .id(orderDetailInfo.getId())
                    .value(orderDetailInfo.getValue() + orderReq.getValue())
                    .updateTime(new Date())
                    .build();
            orderDetailInfoMapper.updateByPrimaryKeySelective(build1);
        } else {
            // 创建订单
            OrderDetailInfo build = OrderDetailInfo.builder()
                    .userId(orderReq.getUserId())
                    .orderId(orderReq.getOrderId())
                    .preOrderId(orderReq.getPreOrderId())
                    .name(userInfo.getName())
                    .phone(userInfo.getPhone())
                    .value(orderReq.getValue())
                    .coach(preOrderInfo.getCoach())
                    .price(preOrderInfo.getPrice())
                    .content(preOrderInfo.getContent())
                    .isDel(BaseConstant.NO)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            orderDetailInfoMapper.insertSelective(build);
        }
        return "success";
    }

    @Override
    public String updateOrder(OrderReq orderReq) throws ServiceException {

        isAdmin();
        OrderDetailInfo orderDetailInfo = orderDetailInfoMapper.selectByUserIdAndPreOrderId(orderReq.getUserId(), orderReq.getPreOrderId());
        if (orderDetailInfo == null) {
            throw new ServiceException(ReturnConstant.NO_VALID_ORDER, HttpStatus.BAD_REQUEST.value());
        }
        // 增加或减少订单次数
        OrderDetailInfo build1 = OrderDetailInfo.builder()
                .id(orderDetailInfo.getId())
                .value(orderReq.getValue())
                .updateTime(new Date())
                .build();
        if (orderReq.getValue() <= 0) {
            build1.setIsDel(BaseConstant.YES);
        }
        orderDetailInfoMapper.updateByPrimaryKeySelective(build1);
        return "success";
    }

    @Override
    public List<PreOrderVo> list(int page, int size) {
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNum(page);
        List<PreOrderVo> preOrderVoList = new ArrayList<>();
        int total = preOrderInfoMapper.selectCount();
        if (pageInfo.hasData(total)) {
            List<PreOrderInfo> preOrderInfoList = preOrderInfoMapper.selectList(pageInfo.getStartIndex(), pageInfo.getPageSize());
            if (!preOrderInfoList.isEmpty()) {
                for (PreOrderInfo preOrderInfo : preOrderInfoList) {
                    PreOrderVo preOrderVo = new PreOrderVo();
                    BeanUtils.copyProperties(preOrderInfo, preOrderVo);

                    preOrderVoList.add(preOrderVo);
                }
            }
        }

        return preOrderVoList;
    }

    @Override
    public List<OrderDetailVo> userOrders(int page, int size) throws ServiceException {
        UserInfo user = JWTUtils.getUser();
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNum(page);
        List<OrderDetailVo> orderDetailVoList = new ArrayList<>();
        int total = orderDetailInfoMapper.selectCountByUserId(user.getId());
        if (pageInfo.hasData(total)) {
            List<OrderDetailInfo> orderDetailInfoList = orderDetailInfoMapper.selectListByUserId(user.getId(), pageInfo.getStartIndex(), pageInfo.getPageSize());
            if (!orderDetailInfoList.isEmpty()) {
                for (OrderDetailInfo orderDetailInfo : orderDetailInfoList) {
                    OrderDetailVo orderDetailVo = new OrderDetailVo();
                    BeanUtils.copyProperties(orderDetailInfo, orderDetailVo);
                    orderDetailVo.setId(null);
                    orderDetailVo.setUserId(null);
                    orderDetailVoList.add(orderDetailVo);
                }
            }
        }
        return orderDetailVoList;
    }

    @Override
    public List<OrderDetailVo> allUserOrders(int page, int size) {
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNum(page);
        List<OrderDetailVo> orderDetailVoList = new ArrayList<>();
        int total = orderDetailInfoMapper.selectCount();
        if (pageInfo.hasData(total)) {
            List<OrderDetailInfo> orderDetailInfoList = orderDetailInfoMapper.selectList(pageInfo.getStartIndex(), pageInfo.getPageSize());
            if (!orderDetailInfoList.isEmpty()) {
                for (OrderDetailInfo orderDetailInfo : orderDetailInfoList) {
                    OrderDetailVo orderDetailVo = new OrderDetailVo();
                    BeanUtils.copyProperties(orderDetailInfo, orderDetailVo);
                    orderDetailVoList.add(orderDetailVo);
                }
            }
        }
        return orderDetailVoList;
    }

    @Override
    public List<OrderDetailVo> userApples(int type, int page, int size) {
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNum(page);
        List<OrderDetailVo> orderDetailVoList = new ArrayList<>();
        int total = orderInfoMapper.selectCountByType(type, BaseConstant.NO);
        if (pageInfo.hasData(total)) {
            List<OrderInfo> orderInfoList = orderInfoMapper.selectListByType(type, BaseConstant.NO, pageInfo.getStartIndex(), pageInfo.getPageSize());
            if (!orderInfoList.isEmpty()) {
                for (OrderInfo orderInfo : orderInfoList) {

                    OrderDetailVo orderDetailVo = new OrderDetailVo();
                    UserInfo userInfo = userMapper.selectByPrimaryKey(orderInfo.getUserId());

                    orderDetailVo.setOrderId(orderInfo.getId());
                    orderDetailVo.setUserId(userInfo.getId());
                    orderDetailVo.setName(userInfo.getName());
                    orderDetailVo.setPhone(userInfo.getPhone());

                    if (orderInfo.getPreOrderId() != null) {
                        PreOrderInfo preOrderInfo = preOrderInfoMapper.selectByPrimaryKey(orderInfo.getPreOrderId());
                        orderDetailVo.setPreOrderId(preOrderInfo.getId());
                        orderDetailVo.setCoach(preOrderInfo.getCoach());
                        orderDetailVo.setValue(preOrderInfo.getValue());
                        orderDetailVo.setPrice(preOrderInfo.getPrice());
                        orderDetailVo.setContent(preOrderInfo.getContent());
                    }
                    orderDetailVoList.add(orderDetailVo);
                }
            }
        }
        return orderDetailVoList;
    }

    private void isAdmin() throws ServiceException {
        UserInfo user = JWTUtils.getUser();
        UserInfo userInfo = userMapper.selectByPrimaryKey(user.getId());
        if (!userInfo.getAdminStatus().equals(BaseConstant.YES)) {
            throw new ServiceException(ReturnConstant.NO_AUTH, HttpStatus.FORBIDDEN.value());
        }
    }

}
