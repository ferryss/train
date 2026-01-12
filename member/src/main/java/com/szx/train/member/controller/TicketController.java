package com.szx.train.member.controller;

import com.szx.train.common.context.LoginMemberContext;
import com.szx.train.common.resp.CommonResp;
import com.szx.train.common.resp.PageResp;
import com.szx.train.member.domain.dto.TicketQueryReq;
import com.szx.train.member.domain.vo.TicketResp;
import com.szx.train.member.service.ITicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 车票 前端控制器
 * </p>
 *
 * @author Ferry
 * @since 2025-11-19
 */
@RestController
@RequestMapping("/ticket")
@Slf4j
@RequiredArgsConstructor
public class TicketController {

    private final ITicketService ticketService;
    @GetMapping("/query-list")
    public CommonResp<PageResp<TicketResp>> queryList(@Valid TicketQueryReq  req) {
        req.setMemberId(LoginMemberContext.getId());
        PageResp<TicketResp> ticketRespPageResp = ticketService.queryList(req);
        return new CommonResp<>(ticketRespPageResp);
    }
}
