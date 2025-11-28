package com.szx.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szx.train.business.domain.Train;
import com.szx.train.business.domain.TrainCarriage;
import com.szx.train.business.domain.TrainSeat;
import com.szx.train.business.enums.SeatColEnum;
import com.szx.train.business.mapper.TrainMapper;
import com.szx.train.business.req.TrainGenerateReq;
import com.szx.train.business.req.TrainQueryReq;
import com.szx.train.business.req.TrainSaveReq;
import com.szx.train.business.resp.TrainQueryResp;
import com.szx.train.common.resp.PageResp;
import com.szx.train.common.util.SnowUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainService extends ServiceImpl<TrainMapper, Train> {

    private static final Logger LOG = LoggerFactory.getLogger(TrainService.class);

    private final TrainCarriageService trainCarriageService;
    private final TrainSeatService trainSeatService;

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

    public void generateTrainSeat(TrainGenerateReq req){
        String code = req.getCode();
        String type = req.getType();
        //查询所有的车厢
        List<TrainCarriage> list = trainCarriageService.lambdaQuery()
                .eq(TrainCarriage::getTrainCode, code)
                .list();

        if(list.isEmpty()){
            return;
        }


        ArrayList<TrainSeat> trainSeats = new ArrayList<>();
        //循环所有的车厢
        for(TrainCarriage trainCarriage : list){
            Integer index = trainCarriage.getIndex();
            String seatType = trainCarriage.getSeatType();
            Integer row = trainCarriage.getRowCount();
            //根据seatType获取列数
            List<SeatColEnum> colsByType = SeatColEnum.getColsByType(seatType);
            int count = 1; //CarriageSeatIndex 车厢座位索引
            LocalDateTime now = LocalDateTime.now();
            for(int i = 1; i < row + 1; i++){
                for(SeatColEnum col : colsByType){
                    TrainSeat trainSeat = new TrainSeat();
                    //设置属性
                    trainSeat.setId(SnowUtil.getSnowflakeNextId());
                    trainSeat.setTrainCode(code);
                    trainSeat.setCarriageIndex(index);
                    trainSeat.setSeatType(seatType);
                    String rowString = i < 10 ? "0"+i : String.valueOf(i);
                    trainSeat.setRow(rowString);
                    trainSeat.setCol(col.getCode());
                    trainSeat.setCarriageSeatIndex(count++);
                    trainSeat.setCreateTime(now);
                    trainSeat.setUpdateTime(now);
                    //添加到list中
                    trainSeats.add(trainSeat);
                }
            }
        }
        trainSeatService.saveBatch(trainSeats, 500);
    }


}
