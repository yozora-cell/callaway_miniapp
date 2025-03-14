/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.entity.base.ApiResponse;
import com.tencent.wxcloudrun.entity.request.UserReq;
import com.tencent.wxcloudrun.entity.request.WeChatLoginReq;
import com.tencent.wxcloudrun.entity.vo.UserVo;
import com.tencent.wxcloudrun.exception.ServiceException;
import com.tencent.wxcloudrun.service.UserService;
import com.tencent.wxcloudrun.service.WeChatLoginService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author <a href="mailto:chenxilzx1@gmail.com">theonefx</a>
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final WeChatLoginService weChatLoginService;
    private final UserService userService;

    public UserController(WeChatLoginService weChatLoginService, UserService userService) {
        this.weChatLoginService = weChatLoginService;
        this.userService = userService;
    }

    @GetMapping("/userInfo")
    public ApiResponse<UserVo> userInfo() throws ServiceException {
        return new ApiResponse<>(userService.userInfo());
    }

    /**
     * description: 获取用户列表
     *
     * @param page 页码
     * @param size 每页大小
     * @return com.tencent.wxcloudrun.entity.base.ApiResponse<java.util.List < com.tencent.wxcloudrun.entity.vo.UserVo>>
     * @author yozora
     * @date 11:51 2025/3/11
     **/
    @GetMapping("/list/{page}/{size}")
    public ApiResponse<List<UserVo>> list(@PathVariable int page, @PathVariable int size) {
        return new ApiResponse<>(userService.list(page, size));
    }

    /**
     * description: 微信登录
     *
     * @param weChatLoginReq 微信登录请求
     * @return com.tencent.wxcloudrun.entity.request.base.ApiResponse<java.lang.Object>
     * @author yozora
     * @date 16:52 2025/3/9
     **/
    @PostMapping("/weChatLogin")
    public ApiResponse<Object> weChatLogin(@RequestBody WeChatLoginReq weChatLoginReq) throws ServiceException {
        return new ApiResponse<>(weChatLoginService.getOpenIdAndSessionKey(weChatLoginReq));
    }

    /**
     * description: 会员申请
     *
     * @return com.tencent.wxcloudrun.entity.base.ApiResponse<java.lang.Object>
     * @author yozora
     * @date 0:44 2025/3/10
     **/
    @PostMapping("vipApply")
    public ApiResponse<String> vipApply(@RequestBody UserReq userReq) throws ServiceException {
        return new ApiResponse<>(userService.vipApply(userReq));
    }

    /**
     * description: 会员注册
     *
     * @param userReq 用户注册请求
     * @return com.tencent.wxcloudrun.entity.base.ApiResponse<java.lang.String>
     * @author yozora
     * @date 12:14 2025/3/10
     **/
    @PostMapping("registerVip")
    @Deprecated
    public ApiResponse<String> registerVip(@RequestBody UserReq userReq) throws ServiceException {
        return new ApiResponse<>(userService.registerVip(userReq));
    }

}
