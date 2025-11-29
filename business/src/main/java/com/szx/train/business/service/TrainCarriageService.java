package com.szx.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szx.train.business.domain.TrainCarriage;
import com.szx.train.business.mapper.TrainCarriageMapper;
import com.szx.train.business.req.TrainCarriageQueryReq;
import com.szx.train.business.req.TrainCarriageSaveReq;
import com.szx.train.business.resp.TrainCarriageQueryResp;
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
public class TrainCarriageService extends ServiceImpl<TrainCarriageMapper, TrainCarriage> {

    private static final Logger LOG = LoggerFactory.getLogger(TrainCarriageService.class);


    public void saveTrainCarriage(TrainCarriageSaveReq req) {
        //做唯一性判断
        TrainCarriage trainCarriageDB = lambdaQuery()
                .eq(TrainCarriage::getTrainCode, req.getTrainCode())
                .eq(TrainCarriage::getIndex, req.getIndex())
                .one();
        if (ObjectUtil.isNotNull(trainCarriageDB)) {
            throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_CARRIAGE_INDEX_UNIQUE_ERROR);
        }

        LocalDateTime now = LocalDateTime.now();
        TrainCarriage trainCarriage = BeanUtil.copyProperties(req, TrainCarriage.class);
        if (ObjectUtil.isNull(trainCarriage.getId())) {
            trainCarriage.setId(SnowUtil.getSnowflakeNextId());
            trainCarriage.setCreateTime(now);
            trainCarriage.setUpdateTime(now);
            save(trainCarriage);
        } else {
            trainCarriage.setUpdateTime(now);
            updateById(trainCarriage);
        }
    }

    public PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryReq req) {
        IPage<TrainCarriage> page = new Page<>(req.getPage(), req.getSize());

        IPage<TrainCarriage> list = lambdaQuery()
                .eq(StrUtil.isNotBlank(req.getTrainCode()), TrainCarriage::getTrainCode, req.getTrainCode())
                .orderByAsc(TrainCarriage::getTrainCode, TrainCarriage::getIndex)
                .page(page);

        if(list.getRecords().isEmpty()){
        return null;
        }

        List<TrainCarriageQueryResp> trainCarriageQueryRespList = list.getRecords().stream().map(item -> {
            TrainCarriageQueryResp trainCarriageQueryResp = BeanUtil.copyProperties(item, TrainCarriageQueryResp.class);
            return trainCarriageQueryResp;
            }).toList();

        IPage<TrainCarriageQueryResp> trainCarriageQueryRespPage = new Page<>(req.getPage(), req.getSize());
        trainCarriageQueryRespPage.setTotal(list.getTotal());
        trainCarriageQueryRespPage.setRecords(trainCarriageQueryRespList);


        PageResp<TrainCarriageQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(trainCarriageQueryRespPage.getTotal());
        pageResp.setList(trainCarriageQueryRespPage.getRecords());
        return pageResp;
    }

    public void delete(Long id) {
        removeById(id);
    }

    public TrainCarriageQueryResp queryById(Long id) {

                TrainCarriage byId = getById(id);
        if(byId == null){
        return null;
        }

        return BeanUtil.copyProperties(byId, TrainCarriageQueryResp.class);
    }
}
