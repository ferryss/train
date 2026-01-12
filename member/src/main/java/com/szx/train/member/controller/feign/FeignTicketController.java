package com.szx.train.member.controller.feign;


import com.szx.train.common.req.TicketReq;
import com.szx.train.common.resp.CommonResp;
import com.szx.train.member.service.ITicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@RequestMapping("/feign/ticket")
@Slf4j
@RequiredArgsConstructor
public class FeignTicketController {

    private final ITicketService ticketService;

    @PostMapping("/save")
    public CommonResp<Object> saveTicket(@Valid @RequestBody TicketReq req){
        ticketService.saveTicket(req);
        return new CommonResp<>();
    }
}
