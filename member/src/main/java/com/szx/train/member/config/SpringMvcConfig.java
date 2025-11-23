package com.szx.train.member.config;

import com.szx.train.common.interceptor.LogInterceptor;
import com.szx.train.common.interceptor.MemberInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author ferry
 * @date 2025/11/23
 * @project train
 * @description
 */
@Configuration
@RequiredArgsConstructor
public class SpringMvcConfig implements WebMvcConfigurer {

    private final MemberInterceptor memberInterceptor;

    private final LogInterceptor logInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor);

        registry.addInterceptor(memberInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/member/member/code",
                        "/member/member/login"
                );
    }
}
