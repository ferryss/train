package com.szx.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szx.train.business.domain.Station;
import com.szx.train.business.mapper.StationMapper;
import com.szx.train.business.req.StationQueryReq;
import com.szx.train.business.req.StationSaveReq;
import com.szx.train.business.resp.StationQueryResp;
import com.szx.train.common.resp.PageResp;
import com.szx.train.common.util.SnowUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StationService extends ServiceImpl<StationMapper, Station> {

    private static final Logger LOG = LoggerFactory.getLogger(StationService.class);


    public void saveStation(StationSaveReq req) {
        LocalDateTime now = LocalDateTime.now();
        Station station = BeanUtil.copyProperties(req, Station.class);
        if (ObjectUtil.isNull(station.getId())) {
            station.setId(SnowUtil.getSnowflakeNextId());
            station.setCreateTime(now);
            station.setUpdateTime(now);
            save(station);
        } else {
            station.setUpdateTime(now);
            updateById(station);
        }
    }

    public PageResp<StationQueryResp> queryList(StationQueryReq req) {
        IPage<Station> page = new Page<>(req.getPage(), req.getSize());

        IPage<Station> list = lambdaQuery()
            //.eq(LoginMemberContext.getId() != null , Station::getMemberId, LoginMemberContext.getId())
            .orderByDesc(Station::getCreateTime)
            .page(page);

        if(list.getRecords().isEmpty()){
        return null;
        }

        List<StationQueryResp> stationQueryRespList = list.getRecords().stream().map(item -> {
            StationQueryResp stationQueryResp = BeanUtil.copyProperties(item, StationQueryResp.class);
            return stationQueryResp;
            }).toList();

        IPage<StationQueryResp> stationQueryRespPage = new Page<>(req.getPage(), req.getSize());
        stationQueryRespPage.setTotal(list.getTotal());
        stationQueryRespPage.setRecords(stationQueryRespList);


        PageResp<StationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(stationQueryRespPage.getTotal());
        pageResp.setList(stationQueryRespPage.getRecords());
        return pageResp;
    }

    public void delete(Long id) {
        removeById(id);
    }

    public StationQueryResp queryById(Long id) {

                Station byId = getById(id);
        if(byId == null){
        return null;
        }

        return BeanUtil.copyProperties(byId, StationQueryResp.class);
    }
}
