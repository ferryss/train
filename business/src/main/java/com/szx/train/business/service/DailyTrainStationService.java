package com.szx.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szx.train.business.domain.DailyTrainStation;
import com.szx.train.business.mapper.DailyTrainStationMapper;
import com.szx.train.business.req.DailyTrainStationQueryReq;
import com.szx.train.business.req.DailyTrainStationSaveReq;
import com.szx.train.business.resp.DailyTrainStationQueryResp;
import com.szx.train.common.resp.PageResp;
import com.szx.train.common.util.SnowUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DailyTrainStationService extends ServiceImpl<DailyTrainStationMapper, DailyTrainStation> {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainStationService.class);


    public void saveDailyTrainStation(DailyTrainStationSaveReq req) {
        LocalDateTime now = LocalDateTime.now();
        DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(req, DailyTrainStation.class);
        if (ObjectUtil.isNull(dailyTrainStation.getId())) {
            dailyTrainStation.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setUpdateTime(now);
            save(dailyTrainStation);
        } else {
            dailyTrainStation.setUpdateTime(now);
            updateById(dailyTrainStation);
        }
    }

    public PageResp<DailyTrainStationQueryResp> queryList(DailyTrainStationQueryReq req) {
        IPage<DailyTrainStation> page = new Page<>(req.getPage(), req.getSize());

        IPage<DailyTrainStation> list = lambdaQuery()
            //.eq(LoginMemberContext.getId() != null , DailyTrainStation::getMemberId, LoginMemberContext.getId())
            .orderByDesc(DailyTrainStation::getCreateTime)
            .page(page);

        if(list.getRecords().isEmpty()){
        return null;
        }

        List<DailyTrainStationQueryResp> dailyTrainStationQueryRespList = list.getRecords().stream().map(item -> {
            DailyTrainStationQueryResp dailyTrainStationQueryResp = BeanUtil.copyProperties(item, DailyTrainStationQueryResp.class);
            return dailyTrainStationQueryResp;
            }).toList();

        IPage<DailyTrainStationQueryResp> dailyTrainStationQueryRespPage = new Page<>(req.getPage(), req.getSize());
        dailyTrainStationQueryRespPage.setTotal(list.getTotal());
        dailyTrainStationQueryRespPage.setRecords(dailyTrainStationQueryRespList);


        PageResp<DailyTrainStationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(dailyTrainStationQueryRespPage.getTotal());
        pageResp.setList(dailyTrainStationQueryRespPage.getRecords());
        return pageResp;
    }

    public void delete(Long id) {
        removeById(id);
    }

    public DailyTrainStationQueryResp queryById(Long id) {

                DailyTrainStation byId = getById(id);
        if(byId == null){
        return null;
        }

        return BeanUtil.copyProperties(byId, DailyTrainStationQueryResp.class);
    }
}
