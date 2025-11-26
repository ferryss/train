package com.szx.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szx.train.business.domain.DailyTrainTicket;
import com.szx.train.business.mapper.DailyTrainTicketMapper;
import com.szx.train.business.req.DailyTrainTicketQueryReq;
import com.szx.train.business.req.DailyTrainTicketSaveReq;
import com.szx.train.business.resp.DailyTrainTicketQueryResp;
import com.szx.train.common.resp.PageResp;
import com.szx.train.common.util.SnowUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DailyTrainTicketService extends ServiceImpl<DailyTrainTicketMapper, DailyTrainTicket> {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainTicketService.class);


    public void saveDailyTrainTicket(DailyTrainTicketSaveReq req) {
        LocalDateTime now = LocalDateTime.now();
        DailyTrainTicket dailyTrainTicket = BeanUtil.copyProperties(req, DailyTrainTicket.class);
        if (ObjectUtil.isNull(dailyTrainTicket.getId())) {
            dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainTicket.setCreateTime(now);
            dailyTrainTicket.setUpdateTime(now);
            save(dailyTrainTicket);
        } else {
            dailyTrainTicket.setUpdateTime(now);
            updateById(dailyTrainTicket);
        }
    }

    public PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq req) {
        IPage<DailyTrainTicket> page = new Page<>(req.getPage(), req.getSize());

        IPage<DailyTrainTicket> list = lambdaQuery()
//            .eq(LoginMemberContext.getId() != null , DailyTrainTicket::getMemberId, LoginMemberContext.getId())
            .orderByDesc(DailyTrainTicket::getCreateTime)
            .page(page);

        if(list.getRecords().isEmpty()){
        return null;
        }

        List<DailyTrainTicketQueryResp> dailyTrainTicketQueryRespList = list.getRecords().stream().map(item -> {
            DailyTrainTicketQueryResp dailyTrainTicketQueryResp = BeanUtil.copyProperties(item, DailyTrainTicketQueryResp.class);
            return dailyTrainTicketQueryResp;
            }).toList();

        IPage<DailyTrainTicketQueryResp> dailyTrainTicketQueryRespPage = new Page<>(req.getPage(), req.getSize());
        dailyTrainTicketQueryRespPage.setTotal(list.getTotal());
        dailyTrainTicketQueryRespPage.setRecords(dailyTrainTicketQueryRespList);


        PageResp<DailyTrainTicketQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(dailyTrainTicketQueryRespPage.getTotal());
        pageResp.setList(dailyTrainTicketQueryRespPage.getRecords());
        return pageResp;
    }

    public void delete(Long id) {
        removeById(id);
    }

    public DailyTrainTicketQueryResp queryById(Long id) {

                DailyTrainTicket byId = getById(id);
        if(byId == null){
        return null;
        }

        return BeanUtil.copyProperties(byId, DailyTrainTicketQueryResp.class);
    }
}
