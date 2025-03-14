package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.entity.base.PageInfo;
import com.tencent.wxcloudrun.entity.constant.BaseConstant;
import com.tencent.wxcloudrun.entity.constant.OrderConstant;
import com.tencent.wxcloudrun.entity.dto.OrderInfo;
import com.tencent.wxcloudrun.entity.dto.UserInfo;
import com.tencent.wxcloudrun.entity.request.OrderReq;
import com.tencent.wxcloudrun.entity.request.UserReq;
import com.tencent.wxcloudrun.entity.vo.UserVo;
import com.tencent.wxcloudrun.exception.ServiceException;
import com.tencent.wxcloudrun.dao.UserMapper;
import com.tencent.wxcloudrun.service.OrderService;
import com.tencent.wxcloudrun.service.UserService;
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
 * description
 * @version 1.0
 * @date 2025/03/10 0:25
 */
@Service
public class UserServiceImpl implements UserService {


    private final OrderService orderService;
    private final UserMapper userMapper;

    public UserServiceImpl(OrderService orderService, UserMapper userMapper) {
        this.orderService = orderService;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String vipApply(UserReq userReq) throws ServiceException {
        UserInfo user = JWTUtils.getUser();
        UserInfo build1 = UserInfo.builder()
                .id(user.getId())
                .name(user.getName())
                .updateTime(user.getUpdateTime())
                .build();
        userMapper.updateByPrimaryKeySelective(build1);

        OrderReq orderReq = new OrderReq();
        orderReq.setUserId(user.getId());
        orderReq.setOrderType(OrderConstant.APPLY_ORDER);
        orderReq.setIsActive(BaseConstant.NO);
        OrderInfo orderInfo = orderService.selectOrderInfoByType(orderReq);
        if (orderInfo != null) {
            throw new ServiceException("already apply", HttpStatus.BAD_REQUEST.value());
        }
        OrderInfo build = OrderInfo.builder()
                .userId(user.getId())
                .orderType(OrderConstant.APPLY_ORDER)
                .isActive(BaseConstant.NO)
                .isDel(BaseConstant.NO)
                .createTime(new Date())
                .updateTime(new Date())
                .build();
        orderService.insertOrderInfo(build);
        return "success";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String registerVip(UserReq userReq) {
        return "success";
    }

    @Override
    public List<UserVo> list(int page, int size) {
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNum(page);
        List<UserVo> userVoList = new ArrayList<>();
        int total = userMapper.selectCount();
        if (pageInfo.hasData(total)) {
            List<UserInfo> userInfoList = userMapper.selectList(pageInfo.getStartIndex(), pageInfo.getPageSize());
            if (!userInfoList.isEmpty()) {
                for (UserInfo userInfo : userInfoList) {
                    UserVo userVo = new UserVo();
                    BeanUtils.copyProperties(userInfo, userVo);

                    userVoList.add(userVo);
                }
            }
        }

        return userVoList;
    }
}
