package com.szx.train.member.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szx.train.common.exception.BusinessException;
import com.szx.train.common.util.JwtUtil;
import com.szx.train.member.domain.dto.MemberDTO;
import com.szx.train.member.domain.po.Member;
import com.szx.train.common.resp.MemberLoginVO;
import com.szx.train.member.mapper.MemberMapper;
import com.szx.train.member.service.IMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.szx.train.common.exception.BusinessExceptionEnum.MEMBER_MOBILE_CODE_ERROR;
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
@Slf4j
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements IMemberService {

    @Override
    public MemberLoginVO register(MemberDTO memberDTO) {
        //判断是否已经注册
        String mobile = memberDTO.getMobile();
        List<Member> list = lambdaQuery().eq(Member::getMobile, mobile).list();

        //如果手机号不存在
        if(CollUtil.isEmpty(list)){
            throw new BusinessException(MEMBER_MOBILE_EXIST);
        }

        //校验验证码
        if(!memberDTO.getCode().equals("8888")){
            throw new BusinessException(MEMBER_MOBILE_CODE_ERROR);
        }
        Member member = list.get(0);
//        Member member = new Member();
//        member.setMobile(mobile);
//        member.setId(SnowUtil.getSnowflakeNextId());
        //save(member);
        MemberLoginVO memberLoginVO = new MemberLoginVO();
        BeanUtils.copyProperties(member, memberLoginVO);
        //生成token
        String token = JwtUtil.createToken(member.getId(), member.getMobile());
        memberLoginVO.setToken(token);

        return memberLoginVO;
    }
}
