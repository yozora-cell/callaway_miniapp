package com.tencent.wxcloudrun.core.Interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author yozora
 * Description interceptorRegistration
 **/
@Configuration
public class InterceptorRegistration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticationInterceptor());
        // 添加路径需排除server.servlet.context-path
//        registry.addInterceptor(getAccessLimitInterceptor()).addPathPatterns("/**");
    }
}
