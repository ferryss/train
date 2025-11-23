package com.szx.train.common.context;

import com.szx.train.common.resp.MemberLoginVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginMemberContext {
    private static final Logger LOG = LoggerFactory.getLogger(LoginMemberContext.class);

    private static ThreadLocal<MemberLoginVO> member = new ThreadLocal<>();

    public static MemberLoginVO getMember() {
        return member.get();
    }

    public static void setMember(MemberLoginVO member) {
        LoginMemberContext.member.set(member);
    }

    public static Long getId() {
        try {
            return member.get().getId();
        } catch (Exception e) {
            LOG.error("获取登录会员信息异常", e);
            throw e;
        }
    }

}
