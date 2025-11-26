package com.szx.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szx.train.business.domain.DailyTrainSeat;
import com.szx.train.business.mapper.DailyTrainSeatMapper;
import com.szx.train.business.req.DailyTrainSeatQueryReq;
import com.szx.train.business.req.DailyTrainSeatSaveReq;
import com.szx.train.business.resp.DailyTrainSeatQueryResp;
import com.szx.train.common.resp.PageResp;
import com.szx.train.common.util.SnowUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DailyTrainSeatService extends ServiceImpl<DailyTrainSeatMapper, DailyTrainSeat> {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainSeatService.class);


    public void saveDailyTrainSeat(DailyTrainSeatSaveReq req) {
        LocalDateTime now = LocalDateTime.now();
        DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(req, DailyTrainSeat.class);
        if (ObjectUtil.isNull(dailyTrainSeat.getId())) {
            dailyTrainSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);
            save(dailyTrainSeat);
        } else {
            dailyTrainSeat.setUpdateTime(now);
            updateById(dailyTrainSeat);
        }
    }

    public PageResp<DailyTrainSeatQueryResp> queryList(DailyTrainSeatQueryReq req) {
        IPage<DailyTrainSeat> page = new Page<>(req.getPage(), req.getSize());

        IPage<DailyTrainSeat> list = lambdaQuery()
            //.eq(LoginMemberContext.getId() != null , DailyTrainSeat::getMemberId, LoginMemberContext.getId())
            .orderByDesc(DailyTrainSeat::getCreateTime)
            .page(page);

        if(list.getRecords().isEmpty()){
        return null;
        }

        List<DailyTrainSeatQueryResp> dailyTrainSeatQueryRespList = list.getRecords().stream().map(item -> {
            DailyTrainSeatQueryResp dailyTrainSeatQueryResp = BeanUtil.copyProperties(item, DailyTrainSeatQueryResp.class);
            return dailyTrainSeatQueryResp;
            }).toList();

        IPage<DailyTrainSeatQueryResp> dailyTrainSeatQueryRespPage = new Page<>(req.getPage(), req.getSize());
        dailyTrainSeatQueryRespPage.setTotal(list.getTotal());
        dailyTrainSeatQueryRespPage.setRecords(dailyTrainSeatQueryRespList);


        PageResp<DailyTrainSeatQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(dailyTrainSeatQueryRespPage.getTotal());
        pageResp.setList(dailyTrainSeatQueryRespPage.getRecords());
        return pageResp;
    }

    public void delete(Long id) {
        removeById(id);
    }

    public DailyTrainSeatQueryResp queryById(Long id) {

                DailyTrainSeat byId = getById(id);
        if(byId == null){
        return null;
        }

        return BeanUtil.copyProperties(byId, DailyTrainSeatQueryResp.class);
    }
}
