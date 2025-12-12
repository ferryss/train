package com.szx.train.business.config;

import com.szx.train.common.interceptor.LogInterceptor;
import com.szx.train.common.interceptor.MemberInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class SpringMvcConfig implements WebMvcConfigurer {

   private final LogInterceptor logInterceptor;
   private final MemberInterceptor memberInterceptor;

   @Override
   public void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor(logInterceptor);

       registry.addInterceptor(memberInterceptor)
              .addPathPatterns("/**")
              .excludePathPatterns(
                      "/business/admin/**"
              );
   }
}
