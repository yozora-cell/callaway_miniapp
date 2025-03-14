package com.tencent.wxcloudrun.service;


import com.tencent.wxcloudrun.entity.request.UserReq;
import com.tencent.wxcloudrun.entity.vo.UserVo;
import com.tencent.wxcloudrun.exception.ServiceException;

import java.util.List;

/**
 * @author yozora
 * description
 * @version 1.0
 * @date 2025/03/10 0:25
 */
public interface UserService {

    String vipApply(UserReq userReq) throws ServiceException;

    String registerVip(UserReq userReq);

    List<UserVo> list(int page, int size);

}
