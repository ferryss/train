package com.szx.train.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szx.train.member.domain.dto.MemberDTO;
import com.szx.train.member.domain.po.Member;
import com.szx.train.common.resp.MemberLoginVO;
import com.szx.train.member.domain.vo.PassengerVO;

import java.util.List;

/**
 * <p>
 * 会员 服务类
 * </p>
 *
 * @author Ferry
 * @since 2025-11-19
 */
public interface IMemberService extends IService<Member> {

    MemberLoginVO register(MemberDTO memberDTO);

    List<PassengerVO> queryPassenger();
}
