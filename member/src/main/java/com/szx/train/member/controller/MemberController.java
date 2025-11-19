package com.szx.train.member.controller;


import com.szx.train.common.resp.CommonResp;
import com.szx.train.member.domain.dto.MemberDTO;
import com.szx.train.member.domain.po.Member;
import com.szx.train.member.service.IMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    public CommonResp<Long> register(@RequestBody MemberDTO memberDTO){
        return new CommonResp<>(memberService.register(memberDTO));
    }

}
