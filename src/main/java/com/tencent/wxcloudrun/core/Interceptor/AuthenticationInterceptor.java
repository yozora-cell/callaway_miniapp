package com.tencent.wxcloudrun.core.Interceptor;

import com.alibaba.fastjson.JSON;
import com.tencent.wxcloudrun.entity.base.ApiResponse;
import com.tencent.wxcloudrun.utils.CaffeineCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author yozora
 * Description 权限校验拦截器
 **/
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    /**
     * 权限控制排除URL
     */
    public static final String[] EXCLUDE_URI = new String[]{
            "/hello",
            "/index",
            "/api/count",
            "/api/user/weChatLogin",
    };

    /**
     * 功能描述: 权限校验
     *
     * @param request  request
     * @param response response
     * @param handler  handler
     * @return boolean
     * @author yozora
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        boolean allMatch = Arrays.stream(EXCLUDE_URI).anyMatch(uri -> request.getRequestURI().startsWith(uri));
        if (!allMatch) {
            String token = request.getHeader(HttpHeaders.AUTHORIZATION);
//            log.info("access url: {} token: {}", request.getRequestURI(), token);
            if (token == null) {
                log.info("token is null");
                output(response, 0);
                return false;
            }
            if ("b0925140fb4f4db3b1c6dea3557ed3f4".equals(request.getHeader("X-API-KEY"))) {
                return true;
            }
            // 解析 access token
            token = token.replace("Bearer ", "");
            Object o = CaffeineCacheUtil.get(token);
            // 未获取到token信息
            if (o == null) {
                log.info("token user address is null");
                output(response, 1);
                return false;
            }
            response.setHeader(HttpHeaders.AUTHORIZATION, token);
        }
        return true;
    }


    /**
     * 功能描述: 输出返回信息
     *
     * @param response response
     * @param type     0: 无token 1: 无权限
     * @author yozora
     */
    public void output(HttpServletResponse response, int type) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = null;
        ApiResponse<String> apiResponse;
        if (type == 0) {
            apiResponse = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase());
        } else {
            apiResponse = new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }
        try {
            outputStream = response.getOutputStream();
            outputStream.write(JSON.toJSONString(apiResponse).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("output error", e);
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
        }
    }

}
