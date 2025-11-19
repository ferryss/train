package com.szx.train.member.controller;


import com.szx.train.member.po.Member;
import com.szx.train.member.service.IMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Member queryMemberById(@PathVariable Long id){
        log.info("查询会员信息:{}",id);
        return memberService.getById(id);
    }

}
