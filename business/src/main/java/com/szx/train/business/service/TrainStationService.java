package com.szx.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szx.train.business.domain.TrainStation;
import com.szx.train.business.mapper.TrainStationMapper;
import com.szx.train.business.req.TrainStationQueryReq;
import com.szx.train.business.req.TrainStationSaveReq;
import com.szx.train.business.resp.TrainStationQueryResp;
import com.szx.train.common.exception.BusinessException;
import com.szx.train.common.exception.BusinessExceptionEnum;
import com.szx.train.common.resp.PageResp;
import com.szx.train.common.util.SnowUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrainStationService extends ServiceImpl<TrainStationMapper, TrainStation> {

    private static final Logger LOG = LoggerFactory.getLogger(TrainStationService.class);


    public void saveTrainStation(TrainStationSaveReq req) {
        //做唯一性判断
        List<TrainStation> trainStationDBlist = lambdaQuery()
                .eq(TrainStation::getTrainCode, req.getTrainCode())
                .list();
        if (trainStationDBlist != null && !trainStationDBlist.isEmpty()) {
            for(TrainStation trainStationDB : trainStationDBlist){
                if(trainStationDB.getIndex().equals(req.getIndex())){
                    throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_STATION_INDEX_UNIQUE_ERROR);
                }
                if(trainStationDB.getName().equals(req.getName())){
                    throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_STATION_NAME_UNIQUE_ERROR);
                }
            }
        }

        LocalDateTime now = LocalDateTime.now();
        TrainStation trainStation = BeanUtil.copyProperties(req, TrainStation.class);
        if (ObjectUtil.isNull(trainStation.getId())) {
            trainStation.setId(SnowUtil.getSnowflakeNextId());
            trainStation.setCreateTime(now);
            trainStation.setUpdateTime(now);
            save(trainStation);
        } else {
            trainStation.setUpdateTime(now);
            updateById(trainStation);
        }
    }

    public PageResp<TrainStationQueryResp> queryList(TrainStationQueryReq req) {
        IPage<TrainStation> page = new Page<>(req.getPage(), req.getSize());

        IPage<TrainStation> list = lambdaQuery()
                .eq(StrUtil.isNotBlank(req.getTrainCode()), TrainStation::getTrainCode, req.getTrainCode())
                .orderByAsc(TrainStation::getTrainCode, TrainStation::getIndex)
                .page(page);

        if(list.getRecords().isEmpty()){
        return null;
        }

        List<TrainStationQueryResp> trainStationQueryRespList = list.getRecords().stream().map(item -> {
            TrainStationQueryResp trainStationQueryResp = BeanUtil.copyProperties(item, TrainStationQueryResp.class);
            return trainStationQueryResp;
            }).toList();

        IPage<TrainStationQueryResp> trainStationQueryRespPage = new Page<>(req.getPage(), req.getSize());
        trainStationQueryRespPage.setTotal(list.getTotal());
        trainStationQueryRespPage.setRecords(trainStationQueryRespList);


        PageResp<TrainStationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(trainStationQueryRespPage.getTotal());
        pageResp.setList(trainStationQueryRespPage.getRecords());
        return pageResp;
    }

    public void delete(Long id) {
        removeById(id);
    }

    public TrainStationQueryResp queryById(Long id) {

        TrainStation byId = getById(id);
        if(byId == null){
        return null;
        }

        return BeanUtil.copyProperties(byId, TrainStationQueryResp.class);
    }

    public TrainStationQueryResp queryByTrainCodeAndIndex(String trainCode, Integer index) {
        TrainStation one = lambdaQuery()
                .eq(StrUtil.isNotBlank(trainCode), TrainStation::getTrainCode, trainCode)
                .eq(index != null, TrainStation::getIndex, index)
                .one();
        return BeanUtil.copyProperties(one, TrainStationQueryResp.class);
    }
}
