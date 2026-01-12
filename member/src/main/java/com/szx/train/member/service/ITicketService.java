package com.szx.train.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szx.train.common.req.TicketReq;
import com.szx.train.common.resp.PageResp;
import com.szx.train.member.domain.dto.TicketQueryReq;
import com.szx.train.member.domain.po.Ticket;
import com.szx.train.member.domain.vo.TicketResp;

/**
 * <p>
 * 车票 服务类
 * </p>
 *
 * @author Ferry
 * @since 2026-01-11
 */
public interface ITicketService extends IService<Ticket> {

    void saveTicket(TicketReq ticketReq);

    PageResp<TicketResp> queryList(TicketQueryReq ticketQueryReq);

    TicketResp queryById(Long id);
}
