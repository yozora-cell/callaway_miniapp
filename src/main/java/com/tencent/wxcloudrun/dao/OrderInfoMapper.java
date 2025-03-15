package com.tencent.wxcloudrun.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.tencent.wxcloudrun.entity.dto.OrderInfo;
import com.tencent.wxcloudrun.entity.request.OrderReq;

import java.util.List;

@Mapper
public interface OrderInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderInfo record);

    int insertSelective(OrderInfo record);

    OrderInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderInfo record);

    int updateByPrimaryKey(OrderInfo record);

    OrderInfo selectOrderInfoByType(OrderReq orderReq);

    int selectCountByType(@Param("orderType") int type, @Param("isActive") String isActive);

    List<OrderInfo> selectListByType(@Param("orderType") int type, @Param("isActive") String isActive, @Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);
}