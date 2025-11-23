package com.szx.train.member.controller;


import cn.hutool.core.util.StrUtil;
import com.szx.train.common.exception.BusinessException;
import com.szx.train.common.resp.CommonResp;
import com.szx.train.member.domain.dto.MemberDTO;
import com.szx.train.member.domain.po.Member;
import com.szx.train.common.resp.MemberLoginVO;
import com.szx.train.member.service.IMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.szx.train.common.exception.BusinessExceptionEnum.MEMBER_MOBILE_BLANK;

/**
 * <p>
 * 会员 前端控制器
 * </p>
 *
 * @author Ferry
 * @since 2025-11-19
 */
@RestController
@RequestMapping("/member")
@Slf4j
@RequiredArgsConstructor
public class MemberController {

    private final IMemberService memberService;

    @GetMapping("/{id}")
    public CommonResp<Member> queryMemberById(@PathVariable Long id){
        return new CommonResp<>(memberService.getById(id));
    }

    @PostMapping("/register")
    public CommonResp<MemberLoginVO> register(@RequestBody @Valid MemberDTO memberDTO){
        return new CommonResp<>(memberService.register(memberDTO));
    }

    @GetMapping("/code")
    public CommonResp<String> sendCode(String mobile){
        if(StrUtil.isBlank(mobile)){
            throw new BusinessException(MEMBER_MOBILE_BLANK);
        }
        //发送验证码
        log.info("手机号{}的验证码为{}", mobile, 8888);
        return new CommonResp<>("8888");
    }

}
