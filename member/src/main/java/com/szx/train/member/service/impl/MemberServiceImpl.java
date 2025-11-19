package com.szx.train.member.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szx.train.common.exception.BusinessException;
import com.szx.train.member.domain.dto.MemberDTO;
import com.szx.train.member.domain.po.Member;
import com.szx.train.member.mapper.MemberMapper;
import com.szx.train.member.service.IMemberService;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.szx.train.common.exception.BusinessExceptionEnum.MEMBER_MOBILE_EXIST;

/**
 * <p>
 * 会员 服务实现类
 * </p>
 *
 * @author Ferry
 * @since 2025-11-19
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements IMemberService {

    @Override
    public long register(MemberDTO memberDTO) {
        //判断是否已经注册
        String mobile = memberDTO.getMobile();
        List<Member> list = lambdaQuery().eq(Member::getMobile, mobile).list();

        if(CollUtil.isNotEmpty(list)){
            throw new BusinessException(MEMBER_MOBILE_EXIST);
        }

        Member member = new Member();
        member.setMobile(mobile);
        member.setId(System.currentTimeMillis());
        save(member);

        return member.getId();
    }
}
