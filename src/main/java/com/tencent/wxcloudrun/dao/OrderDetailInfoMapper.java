package com.tencent.wxcloudrun.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.tencent.wxcloudrun.entity.dto.OrderDetailInfo;

import java.util.List;

@Mapper
public interface OrderDetailInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderDetailInfo record);

    int insertSelective(OrderDetailInfo record);

    OrderDetailInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderDetailInfo record);

    int updateByPrimaryKey(OrderDetailInfo record);

    OrderDetailInfo selectByUserIdAndPreOrderId(@Param("userId") Long userId, @Param("preOrderId") Integer preOrderId);

    int selectCountByUserId(@Param("userId") Long userId);

    List<OrderDetailInfo> selectListByUserId(@Param("userId") Long userId, @Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);

    int selectCount();

    List<OrderDetailInfo> selectList(@Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);
}