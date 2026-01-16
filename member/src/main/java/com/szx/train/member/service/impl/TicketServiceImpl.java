package com.szx.train.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szx.train.common.req.TicketReq;
import com.szx.train.common.resp.PageResp;
import com.szx.train.common.util.SnowUtil;
import com.szx.train.member.domain.dto.TicketQueryReq;
import com.szx.train.member.domain.po.Ticket;
import com.szx.train.member.domain.vo.TicketResp;
import com.szx.train.member.mapper.TicketMapper;
import com.szx.train.member.service.ITicketService;
import io.seata.core.context.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 车票 服务实现类
 * </p>
 *
 * @author Ferry
 * @since 2026-01-11
 */
@Service
public class TicketServiceImpl extends ServiceImpl<TicketMapper, Ticket> implements ITicketService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketServiceImpl.class);

    public void saveTicket(TicketReq req) {
        LOG.info("seata全局事务ID为 {}", RootContext.getXID());
        LocalDateTime now = LocalDateTime.now();
        Ticket ticket = BeanUtil.copyProperties(req, Ticket.class);

        ticket.setId(SnowUtil.getSnowflakeNextId());
        ticket.setCreateTime(now);
        ticket.setUpdateTime(now);
        save(ticket);
    }

    public PageResp<TicketResp> queryList(TicketQueryReq req) {
        IPage<Ticket> page = new Page<>(req.getPage(), req.getSize());

        IPage<Ticket> list = lambdaQuery()
                .eq(req.getMemberId() != null, Ticket::getMemberId, req.getMemberId())
                .orderByDesc(Ticket::getCreateTime)
                .page(page);

        if(list.getRecords().isEmpty()){
            return null;
        }

        List<TicketResp> ticketRespList = list.getRecords().stream().map(item -> {
            TicketResp ticketResp = BeanUtil.copyProperties(item, TicketResp.class);
            return ticketResp;
        }).toList();

        IPage<TicketResp> trainSeatQueryRespPage = new Page<>(req.getPage(), req.getSize());
        trainSeatQueryRespPage.setTotal(list.getTotal());
        trainSeatQueryRespPage.setRecords(ticketRespList);


        PageResp<TicketResp> pageResp = new PageResp<>();
        pageResp.setTotal(trainSeatQueryRespPage.getTotal());
        pageResp.setList(trainSeatQueryRespPage.getRecords());
        return pageResp;
    }

    public void delete(Long id) {
        removeById(id);
    }

    public TicketResp queryById(Long id) {

        Ticket byId = getById(id);
        if(byId == null){
            return null;
        }

        return BeanUtil.copyProperties(byId, TicketResp.class);
    }
}
