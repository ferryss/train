package com.szx.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szx.train.business.domain.DailyTrainSeat;
import com.szx.train.business.domain.TrainSeat;
import com.szx.train.business.enums.SeatTypeEnum;
import com.szx.train.business.mapper.DailyTrainSeatMapper;
import com.szx.train.business.req.DailyTrainSeatQueryReq;
import com.szx.train.business.req.DailyTrainSeatSaveReq;
import com.szx.train.business.resp.DailyTrainSeatQueryResp;
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
public class DailyTrainSeatService extends ServiceImpl<DailyTrainSeatMapper, DailyTrainSeat> {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainSeatService.class);

    @Autowired
    private TrainSeatService trainSeatService;

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
                .eq(req.getDate() != null, DailyTrainSeat::getDate, req.getDate())
                .eq(StrUtil.isNotBlank(req.getTrainCode()), DailyTrainSeat::getTrainCode, req.getTrainCode())
                .orderByAsc(DailyTrainSeat::getDate, DailyTrainSeat::getTrainCode,
                        DailyTrainSeat::getCarriageIndex, DailyTrainSeat::getCarriageSeatIndex)
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

    @Transactional
    public void genDaily(Date date, String trainCode, Long trainStationCount) {
        LOG.info("开始生成日期【{}】车次【{}】的每日车座", DateUtil.format(date, "yyyy-MM-dd"), trainCode);

        List<TrainSeat> list = trainSeatService.lambdaQuery()
                .eq(TrainSeat::getTrainCode, trainCode)
                .list();

        if(list.isEmpty()){
            LOG.info("车次没有座位数据，生成结束");
            return;
        }

        List<DailyTrainSeat> dailyTrainSeatList = list.stream().map(item -> {
            LocalDateTime now = LocalDateTime.now();
            DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(item, DailyTrainSeat.class);
            dailyTrainSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainSeat.setDate(date);
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);
            String sell = StrUtil.fillBefore("", '0', trainStationCount.intValue()-1);
            dailyTrainSeat.setSell(sell);
            return dailyTrainSeat;
        }).toList();

        //清空当前车次的座位
        lambdaUpdate()
                .eq(DailyTrainSeat::getTrainCode, trainCode)
                .eq(DailyTrainSeat::getDate, date)
                .remove();

        saveBatch(dailyTrainSeatList, 500);
        LOG.info("✅结束生成日期【{}】车次【{}】的每日车座", DateUtil.format(date, "yyyy-MM-dd"), trainCode);
    }

    public int SeatCount(Date date, String trainCode, SeatTypeEnum seatType){
        Long count = lambdaQuery()
                .eq(DailyTrainSeat::getDate, date)
                .eq(DailyTrainSeat::getTrainCode, trainCode)
                .eq(DailyTrainSeat::getSeatType, seatType.getCode())
                .count();

        return count == 0L ? -1 : count.intValue();
    }
}
