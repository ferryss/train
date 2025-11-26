package com.szx.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szx.train.business.domain.TrainSeat;
import com.szx.train.business.mapper.TrainSeatMapper;
import com.szx.train.business.req.TrainSeatQueryReq;
import com.szx.train.business.req.TrainSeatSaveReq;
import com.szx.train.business.resp.TrainSeatQueryResp;
import com.szx.train.common.resp.PageResp;
import com.szx.train.common.util.SnowUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrainSeatService extends ServiceImpl<TrainSeatMapper, TrainSeat> {

    private static final Logger LOG = LoggerFactory.getLogger(TrainSeatService.class);


    public void saveTrainSeat(TrainSeatSaveReq req) {
        LocalDateTime now = LocalDateTime.now();
        TrainSeat trainSeat = BeanUtil.copyProperties(req, TrainSeat.class);
        if (ObjectUtil.isNull(trainSeat.getId())) {
            trainSeat.setId(SnowUtil.getSnowflakeNextId());
            trainSeat.setCreateTime(now);
            trainSeat.setUpdateTime(now);
            save(trainSeat);
        } else {
            trainSeat.setUpdateTime(now);
            updateById(trainSeat);
        }
    }

    public PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq req) {
        IPage<TrainSeat> page = new Page<>(req.getPage(), req.getSize());

        IPage<TrainSeat> list = lambdaQuery()
            //.eq(LoginMemberContext.getId() != null , TrainSeat::getMemberId, LoginMemberContext.getId())
            .orderByDesc(TrainSeat::getCreateTime)
            .page(page);

        if(list.getRecords().isEmpty()){
        return null;
        }

        List<TrainSeatQueryResp> trainSeatQueryRespList = list.getRecords().stream().map(item -> {
            TrainSeatQueryResp trainSeatQueryResp = BeanUtil.copyProperties(item, TrainSeatQueryResp.class);
            return trainSeatQueryResp;
            }).toList();

        IPage<TrainSeatQueryResp> trainSeatQueryRespPage = new Page<>(req.getPage(), req.getSize());
        trainSeatQueryRespPage.setTotal(list.getTotal());
        trainSeatQueryRespPage.setRecords(trainSeatQueryRespList);


        PageResp<TrainSeatQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(trainSeatQueryRespPage.getTotal());
        pageResp.setList(trainSeatQueryRespPage.getRecords());
        return pageResp;
    }

    public void delete(Long id) {
        removeById(id);
    }

    public TrainSeatQueryResp queryById(Long id) {

                TrainSeat byId = getById(id);
        if(byId == null){
        return null;
        }

        return BeanUtil.copyProperties(byId, TrainSeatQueryResp.class);
    }
}
