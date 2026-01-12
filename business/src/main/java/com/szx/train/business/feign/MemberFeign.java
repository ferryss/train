package com.szx.train.business.feign;


import com.szx.train.common.req.TicketReq;
import com.szx.train.common.resp.CommonResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "member", url = "http://localhost:8081/member")
public interface MemberFeign {

    @PostMapping("/feign/ticket/save")
    CommonResp<Object> saveTicket(@RequestBody TicketReq req);

}
