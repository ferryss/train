package com.szx.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szx.train.business.domain.DailyTrainStation;
import com.szx.train.business.domain.TrainStation;
import com.szx.train.business.mapper.DailyTrainStationMapper;
import com.szx.train.business.req.DailyTrainStationQueryReq;
import com.szx.train.business.req.DailyTrainStationSaveReq;
import com.szx.train.business.resp.DailyTrainStationQueryResp;
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
public class DailyTrainStationService extends ServiceImpl<DailyTrainStationMapper, DailyTrainStation> {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainStationService.class);

    @Autowired
    private TrainStationService trainStationService;

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
                .eq(req.getDate() != null, DailyTrainStation::getDate, req.getDate())
                .eq(StrUtil.isNotBlank(req.getTrainCode()), DailyTrainStation::getTrainCode, req.getTrainCode())
                .orderByAsc(DailyTrainStation::getDate, DailyTrainStation::getTrainCode, DailyTrainStation::getIndex)
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

    @Transactional
    public void genDaily(Date date, String trainCode) {
        LOG.info("开始生成日期【{}】车次【{}】的站点信息", DateUtil.format(date, "yyyy-MM-dd"), trainCode);
        List<TrainStation> trainStationList = trainStationService.lambdaQuery()
                .eq(TrainStation::getTrainCode, trainCode)
                .orderByAsc(TrainStation::getIndex)
                .list();
        if(trainStationList.isEmpty()){
            LOG.info("该车次没有站点，生成站点信息结束");
            return;
        }
        List<DailyTrainStation> dailyTrainStationList = trainStationList.stream().map(item -> {
            LocalDateTime now = LocalDateTime.now();
            DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(item, DailyTrainStation.class);
            dailyTrainStation.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainStation.setDate(date);
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setUpdateTime(now);
            return dailyTrainStation;
        }).toList();

        lambdaUpdate()
                .eq(DailyTrainStation::getDate, date)
                .eq(DailyTrainStation::getTrainCode, trainCode)
                .remove();

        saveBatch(dailyTrainStationList);

        LOG.info("✅结束生成日期【{}】车次【{}】的站点信息", DateUtil.format(date, "yyyy-MM-dd"), trainCode);
    }
}
