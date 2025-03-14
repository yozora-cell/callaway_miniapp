package com.tencent.wxcloudrun.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.tencent.wxcloudrun.entity.dto.PreOrderInfo;

import java.util.List;

@Mapper
public interface PreOrderInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PreOrderInfo record);

    int insertSelective(PreOrderInfo record);

    PreOrderInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PreOrderInfo record);

    int updateByPrimaryKey(PreOrderInfo record);

    int selectCount();

    List<PreOrderInfo> selectList(@Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);
}