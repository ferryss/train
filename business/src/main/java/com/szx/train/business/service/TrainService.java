package com.szx.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szx.train.business.domain.Train;
import com.szx.train.business.mapper.TrainMapper;
import com.szx.train.business.req.TrainQueryReq;
import com.szx.train.business.req.TrainSaveReq;
import com.szx.train.business.resp.TrainQueryResp;
import com.szx.train.common.resp.PageResp;
import com.szx.train.common.util.SnowUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrainService extends ServiceImpl<TrainMapper, Train> {

    private static final Logger LOG = LoggerFactory.getLogger(TrainService.class);


    public void saveTrain(TrainSaveReq req) {
        LocalDateTime now = LocalDateTime.now();
        Train train = BeanUtil.copyProperties(req, Train.class);
        if (ObjectUtil.isNull(train.getId())) {
            train.setId(SnowUtil.getSnowflakeNextId());
            train.setCreateTime(now);
            train.setUpdateTime(now);
            save(train);
        } else {
            train.setUpdateTime(now);
            updateById(train);
        }
    }

    public PageResp<TrainQueryResp> queryPageList(TrainQueryReq req) {
        IPage<Train> page = new Page<>(req.getPage(), req.getSize());

        IPage<Train> list = lambdaQuery()
            //.eq(LoginMemberContext.getId() != null , Train::getMemberId, LoginMemberContext.getId())
            .orderByDesc(Train::getCreateTime)
            .page(page);

        if(list.getRecords().isEmpty()){
        return null;
        }

        List<TrainQueryResp> trainQueryRespList = list.getRecords().stream().map(item -> {
            TrainQueryResp trainQueryResp = BeanUtil.copyProperties(item, TrainQueryResp.class);
            return trainQueryResp;
            }).toList();

        IPage<TrainQueryResp> trainQueryRespPage = new Page<>(req.getPage(), req.getSize());
        trainQueryRespPage.setTotal(list.getTotal());
        trainQueryRespPage.setRecords(trainQueryRespList);


        PageResp<TrainQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(trainQueryRespPage.getTotal());
        pageResp.setList(trainQueryRespPage.getRecords());
        return pageResp;
    }

    public void delete(Long id) {
        removeById(id);
    }

    public TrainQueryResp queryById(Long id) {

                Train byId = getById(id);
        if(byId == null){
        return null;
        }

        return BeanUtil.copyProperties(byId, TrainQueryResp.class);
    }

    public List<TrainQueryResp> queryList() {

        List<Train> list = lambdaQuery()
                .orderByAsc(Train::getCode)
                .list();

        if(list.isEmpty()){
            return null;
        }

        return list.stream().map(item -> BeanUtil.copyProperties(item, TrainQueryResp.class)).toList();
    }


}
