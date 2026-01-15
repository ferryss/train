package com.szx.train.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szx.train.member.domain.po.Member;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 会员 Mapper 接口
 * </p>
 *
 * @author Ferry
 * @since 2025-11-19
 */
@Mapper
public interface MemberMapper extends BaseMapper<Member> {

}
