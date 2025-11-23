package com.szx.train.common.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.szx.train.common.context.LoginMemberContext;
import com.szx.train.common.resp.MemberLoginVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 拦截器：Spring框架特有的，常用于登录校验，权限校验，请求日志打印
 */
@Component
public class MemberInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(MemberInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取header的user-info参数
        String info = request.getHeader("user-info");
        if (StrUtil.isNotBlank(info)) {
            LOG.info("当前登录会员：{}", info);
            MemberLoginVO member = JSONUtil.toBean(info, MemberLoginVO.class);
            LoginMemberContext.setMember(member);
        }
        return true;
    }

}
