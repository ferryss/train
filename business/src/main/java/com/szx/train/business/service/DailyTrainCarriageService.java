package com.szx.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szx.train.business.domain.DailyTrainCarriage;
import com.szx.train.business.domain.TrainCarriage;
import com.szx.train.business.mapper.DailyTrainCarriageMapper;
import com.szx.train.business.req.DailyTrainCarriageQueryReq;
import com.szx.train.business.req.DailyTrainCarriageSaveReq;
import com.szx.train.business.resp.DailyTrainCarriageQueryResp;
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
public class DailyTrainCarriageService extends ServiceImpl<DailyTrainCarriageMapper, DailyTrainCarriage> {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainCarriageService.class);

    @Autowired
    private TrainCarriageService trainCarriageService;

    public void saveDailyTrainCarriage(DailyTrainCarriageSaveReq req) {
        LocalDateTime now = LocalDateTime.now();
        DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(req, DailyTrainCarriage.class);
        if (ObjectUtil.isNull(dailyTrainCarriage.getId())) {
            dailyTrainCarriage.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainCarriage.setCreateTime(now);
            dailyTrainCarriage.setUpdateTime(now);
            save(dailyTrainCarriage);
        } else {
            dailyTrainCarriage.setUpdateTime(now);
            updateById(dailyTrainCarriage);
        }
    }

    public PageResp<DailyTrainCarriageQueryResp> queryList(DailyTrainCarriageQueryReq req) {
        IPage<DailyTrainCarriage> page = new Page<>(req.getPage(), req.getSize());

        IPage<DailyTrainCarriage> list = lambdaQuery()
                .eq(req.getDate() != null, DailyTrainCarriage::getDate, req.getDate())
                .eq(StrUtil.isNotBlank(req.getTrainCode()), DailyTrainCarriage::getTrainCode, req.getTrainCode())
                .orderByAsc(DailyTrainCarriage::getDate, DailyTrainCarriage::getTrainCode)
                .page(page);

        if(list.getRecords().isEmpty()){
        return null;
        }

        List<DailyTrainCarriageQueryResp> dailyTrainCarriageQueryRespList = list.getRecords().stream().map(item -> {
            DailyTrainCarriageQueryResp dailyTrainCarriageQueryResp = BeanUtil.copyProperties(item, DailyTrainCarriageQueryResp.class);
            return dailyTrainCarriageQueryResp;
            }).toList();

        IPage<DailyTrainCarriageQueryResp> dailyTrainCarriageQueryRespPage = new Page<>(req.getPage(), req.getSize());
        dailyTrainCarriageQueryRespPage.setTotal(list.getTotal());
        dailyTrainCarriageQueryRespPage.setRecords(dailyTrainCarriageQueryRespList);


        PageResp<DailyTrainCarriageQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(dailyTrainCarriageQueryRespPage.getTotal());
        pageResp.setList(dailyTrainCarriageQueryRespPage.getRecords());
        return pageResp;
    }

    public void delete(Long id) {
        removeById(id);
    }

    public DailyTrainCarriageQueryResp queryById(Long id) {

                DailyTrainCarriage byId = getById(id);
        if(byId == null){
        return null;
        }

        return BeanUtil.copyProperties(byId, DailyTrainCarriageQueryResp.class);
    }

    @Transactional
    public void genDaily(Date date, String trainCode) {
        LOG.info("开始生成日期【{}】车次【{}】的每日车厢", DateUtil.format(date, "yyyy-MM-dd"), trainCode);
        List<TrainCarriage> trainCarriageList = trainCarriageService.lambdaQuery()
                .eq(TrainCarriage::getTrainCode, trainCode)
                .orderByAsc(TrainCarriage::getIndex)
                .list();
        if(trainCarriageList.isEmpty()){
            LOG.info("该车次没有车厢基础数据，生成完毕");
            return;
        }

        List<DailyTrainCarriage> dailyTrainCarriageList = trainCarriageList.stream().map(item -> {
            LocalDateTime now = LocalDateTime.now();
            DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(item, DailyTrainCarriage.class);
            dailyTrainCarriage.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainCarriage.setDate(date);
            dailyTrainCarriage.setCreateTime(now);
            dailyTrainCarriage.setUpdateTime(now);
            return dailyTrainCarriage;
        }).toList();

        lambdaUpdate()
                .eq(DailyTrainCarriage::getDate, date)
                .eq(DailyTrainCarriage::getTrainCode, trainCode)
                .remove();

        saveBatch(dailyTrainCarriageList);
        LOG.info("✅结束生成日期【{}】车次【{}】的每日车厢", DateUtil.format(date, "yyyy-MM-dd"), trainCode);

    }
}
