package com.szx.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szx.train.business.domain.DailyTrain;
import com.szx.train.business.domain.Train;
import com.szx.train.business.mapper.DailyTrainMapper;
import com.szx.train.business.req.DailyTrainQueryReq;
import com.szx.train.business.req.DailyTrainSaveReq;
import com.szx.train.business.resp.DailyTrainQueryResp;
import com.szx.train.common.resp.PageResp;
import com.szx.train.common.util.SnowUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class DailyTrainService extends ServiceImpl<DailyTrainMapper, DailyTrain> {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainService.class);

    @Autowired
    private TrainService trainService;
    @Autowired
    private TrainStationService trainStationService;
    @Autowired
    private TrainCarriageService trainCarriageService;
    @Autowired
    private TrainSeatService trainSeatService;

    public void saveDailyTrain(DailyTrainSaveReq req) {
        LocalDateTime now = LocalDateTime.now();
        DailyTrain dailyTrain = BeanUtil.copyProperties(req, DailyTrain.class);
        if (ObjectUtil.isNull(dailyTrain.getId())) {
            dailyTrain.setId(SnowUtil.getSnowflakeNextId());
            dailyTrain.setCreateTime(now);
            dailyTrain.setUpdateTime(now);
            save(dailyTrain);
        } else {
            dailyTrain.setUpdateTime(now);
            updateById(dailyTrain);
        }
    }

    public PageResp<DailyTrainQueryResp> queryList(DailyTrainQueryReq req) {
        IPage<DailyTrain> page = new Page<>(req.getPage(), req.getSize());

        IPage<DailyTrain> list = lambdaQuery()
                .eq(req.getDate() != null, DailyTrain::getDate, req.getDate())
                .eq(StrUtil.isNotBlank(req.getCode()), DailyTrain::getCode, req.getCode())
                .orderByAsc(DailyTrain::getDate, DailyTrain::getCode)
                .page(page);

        if(list.getRecords().isEmpty()){
        return null;
        }

        List<DailyTrainQueryResp> dailyTrainQueryRespList = list.getRecords().stream().map(item -> {
            DailyTrainQueryResp dailyTrainQueryResp = BeanUtil.copyProperties(item, DailyTrainQueryResp.class);
            return dailyTrainQueryResp;
            }).toList();

        IPage<DailyTrainQueryResp> dailyTrainQueryRespPage = new Page<>(req.getPage(), req.getSize());
        dailyTrainQueryRespPage.setTotal(list.getTotal());
        dailyTrainQueryRespPage.setRecords(dailyTrainQueryRespList);


        PageResp<DailyTrainQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(dailyTrainQueryRespPage.getTotal());
        pageResp.setList(dailyTrainQueryRespPage.getRecords());
        return pageResp;
    }

    public void delete(Long id) {
        removeById(id);
    }

    public DailyTrainQueryResp queryById(Long id) {

                DailyTrain byId = getById(id);
        if(byId == null){
        return null;
        }

        return BeanUtil.copyProperties(byId, DailyTrainQueryResp.class);
    }

    @Transactional
    public void genDaily(Date date) {
        // 1.生成每日车次数据
        // 查询所有车次
        List<Train> trainlist = trainService.lambdaQuery().list();
        if(trainlist.isEmpty()){
            LOG.info("没有车次基础数据");
            return;
        }
        // 添加日期字段，并且更新创建时间字段和新增时间字段
        List<DailyTrain> dailyTrainList = trainlist.stream().map(item -> {
            LocalDateTime now = LocalDateTime.now();
            DailyTrain dailyTrain = BeanUtil.copyProperties(item, DailyTrain.class);
            dailyTrain.setId(SnowUtil.getSnowflakeNextId());
            dailyTrain.setDate(date);
            dailyTrain.setCreateTime(now);
            dailyTrain.setUpdateTime(now);
            return dailyTrain;
        }).toList();
        // 删除每日车次数据
        lambdaUpdate()
                .eq(date != null, DailyTrain::getDate, date)
                .remove();
        // 保存每日车次数据
        saveBatch(dailyTrainList, 500);

//        // 2.生成每日车次车站数据
//        // 查询所有车次车站数据
//        List<TrainStation> trainStationlist = trainStationService.lambdaQuery().list();
//        // 添加日期字段，并且更新创建时间字段和新增时间字段
//        List<DailyTrainStation> dailyTrainStationList = trainStationlist.stream().map(item -> {
//            DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(item, DailyTrainStation.class);
//            dailyTrainStation.setId(SnowUtil.getSnowflakeNextId());
//            dailyTrainStation.setDate(date);
//            dailyTrainStation.setCreateTime(LocalDateTime.now());
//            dailyTrainStation.setUpdateTime(LocalDateTime.now());
//            return dailyTrainStation;
//        }).toList();
//
//
//        // 3.生成每日车次车厢数据
//
//        // 4.生成每日车次座位数据
    }

}
