package com.szx.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szx.train.business.domain.DailyTrain;
import com.szx.train.business.domain.Train;
import com.szx.train.business.domain.TrainStation;
import com.szx.train.business.mapper.DailyTrainMapper;
import com.szx.train.business.req.DailyTrainQueryReq;
import com.szx.train.business.req.DailyTrainSaveReq;
import com.szx.train.business.resp.DailyTrainQueryResp;
import com.szx.train.common.resp.PageResp;
import com.szx.train.common.util.SnowUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyTrainService extends ServiceImpl<DailyTrainMapper, DailyTrain> {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainService.class);

    private final TrainService trainService;
    private final TrainStationService trainStationService;
    private final DailyTrainStationService dailyTrainStationService;
    private final DailyTrainCarriageService dailyTrainCarriageService;
    private final DailyTrainSeatService dailyTrainSeatService;
    private final DailyTrainTicketService dailyTicketService;

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
        LOG.info("开始生成日期：【{}】车次数据", DateUtil.format(date, "yyyy-MM-dd"));
        ArrayList<String> trainCodeList = new ArrayList<>();
        // 添加日期字段，并且更新创建时间字段和新增时间字段
        List<DailyTrain> dailyTrainList = trainlist.stream().map(item -> {
            trainCodeList.add(item.getCode());
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

        for(String trainCode : trainCodeList){
            // 2.生成每日车站数据
            dailyTrainStationService.genDaily(date, trainCode);
            // 3.生成每日车厢数据
            dailyTrainCarriageService.genDaily(date, trainCode);
            // 4.生成每日座位数据
            Long trainStationCount = trainStationService.lambdaQuery()
                    .eq(TrainStation::getTrainCode, trainCode)
                    .count();
            dailyTrainSeatService.genDaily(date, trainCode, trainStationCount);
            // 5.生成每日车票数据
            dailyTicketService.genDaily(date, trainCode);
        }

        LOG.info("✅结束生成日期：【{}】车次数据", DateUtil.format(date, "yyyy-MM-dd"));


    }

}
