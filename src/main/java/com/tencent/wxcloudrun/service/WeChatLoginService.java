package com.tencent.wxcloudrun.service;

import com.alibaba.fastjson2.JSONObject;
import com.tencent.wxcloudrun.dao.UserMapper;
import com.tencent.wxcloudrun.entity.constant.BaseConstant;
import com.tencent.wxcloudrun.entity.dto.UserInfo;
import com.tencent.wxcloudrun.entity.request.WeChatLoginReq;
import com.tencent.wxcloudrun.exception.ServiceException;
import com.tencent.wxcloudrun.utils.JWTUtils;
import com.tencent.wxcloudrun.utils.OkHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author yozora
 * description 微信登录
 * @version 1.0
 */
@Service
@Slf4j
public class WeChatLoginService {

    private final UserMapper userMapper;

    @Value("${wechat.appId}")
    private String appId;

    @Value("${wechat.appSecret}")
    private String appSecret;

    public WeChatLoginService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public Map<String, Object> getOpenIdAndSessionKey(WeChatLoginReq weChatLoginReq) throws ServiceException {
        String sync = OkHttpUtil.builder(false)
                .url("https://api.weixin.qq.com/sns/jscode2session")
                .addParam("appid", appId)
                .addParam("secret", appSecret)
                .addParam("js_code", weChatLoginReq.getCode())
                .addParam("grant_type", "authorization_code")
                .get().sync();

        log.info("wechat login: {}", sync);
        JSONObject res = JSONObject.parseObject(sync);
        String sessionKey = res.getString("openid");
        if (sessionKey != null) {
            UserInfo userInfo = userMapper.selectByOpenId(res.getString("openid"));
            if (userInfo == null) {
                UserInfo build = UserInfo.builder()
                        .openId(res.getString("openid"))
                        .name("微信用户_" + UUID.randomUUID().toString().replace("0", "").substring(0, 8))
                        .phone(null)
                        .adminStatus(BaseConstant.NO)
                        .vipApplyStatus(BaseConstant.NO)
                        .vipStatus(BaseConstant.NO)
                        .idDel(BaseConstant.NO)
                        .createTime(new Date())
                        .updateTime(new Date())
                        .build();

                userMapper.insert(build);
                userInfo = userMapper.selectByOpenId(res.getString("openid"));
            }
            String token = JWTUtils.createToken(userInfo, BaseConstant.ACCESS_TOKEN_TIME);
            Map<String, Object> map = new HashMap<>();
//        map.put("openid", res.get("openid"));
//        map.put("session_key", res.get("session_key"));
            map.put("token", token);
            map.put("adminStatus", userInfo.getAdminStatus());
            map.put("vipApplyStatus", userInfo.getVipApplyStatus());
            map.put("vipStatus", userInfo.getVipStatus());
            return map;
        } else {
            throw new ServiceException("wechat login failed", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}
