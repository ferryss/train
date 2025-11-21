package com.szx.train.gateway.interceptor;


import com.szx.train.gateway.config.AuthProperties;
import com.szx.train.gateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final AntPathMatcher  matcher = new AntPathMatcher();
    private final AuthProperties properties;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.获取request
        ServerHttpRequest request = exchange.getRequest();
        // 2.判断是否在拦截路径中
        if(isExclude(request.getPath().toString())){
            return chain.filter(exchange);
        }
        // 2.获取请求头中的 token
        String token = null;
        List<String> list = request.getHeaders().get("Authorization");
        if(list != null && !list.isEmpty()){
            token = list.get(0).substring(7);
        }
        // 3.校验token
        Long userId = null;
        if(JwtUtil.validate(token)){
            userId = JwtUtil.getJSONObject(token).getLong("id");
        }else {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        // 4.存入上下文
        String userIdStr = userId.toString();
        ServerWebExchange webExchange = exchange.mutate()
                .request(builder -> builder.header("user-info", userIdStr))
                .build();

        // 5.放行
        return chain.filter(webExchange);
    }

    private boolean isExclude(String path) {
        for(String excludePath: properties.getExcludePaths()){
            if(matcher.match(excludePath, path)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}