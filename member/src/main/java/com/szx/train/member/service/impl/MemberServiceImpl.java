package com.szx.train.member.service.impl;

import com.szx.train.member.po.Member;
import com.szx.train.member.mapper.MemberMapper;
import com.szx.train.member.service.IMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
