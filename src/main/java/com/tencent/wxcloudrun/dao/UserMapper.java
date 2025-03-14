package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.entity.dto.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yozora
 * description user mapper
 * @version 1.0
 * @date 2025/03/09 20:35
 */
@Mapper
public interface UserMapper {

    int deleteByPrimaryKey(Long id);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);

    UserInfo selectByOpenId(String openid);

    int selectCount();

    List<UserInfo> selectList(@Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);
}
