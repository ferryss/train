package com.szx.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szx.train.business.domain.DailyTrainTicket;
import com.szx.train.business.domain.Train;
import com.szx.train.business.domain.TrainStation;
import com.szx.train.business.enums.SeatTypeEnum;
import com.szx.train.business.enums.TrainTypeEnum;
import com.szx.train.business.mapper.DailyTrainTicketMapper;
import com.szx.train.business.req.DailyTrainTicketQueryReq;
import com.szx.train.business.req.DailyTrainTicketSaveReq;
import com.szx.train.business.resp.DailyTrainTicketQueryResp;
import com.szx.train.common.resp.PageResp;
import com.szx.train.common.util.SnowUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyTrainTicketService extends ServiceImpl<DailyTrainTicketMapper, DailyTrainTicket> {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainTicketService.class);

    private final TrainStationService trainStationService;
    private final DailyTrainSeatService dailyTrainSeatService;
    private final TrainService trainService;

    public void saveDailyTrainTicket(DailyTrainTicketSaveReq req) {
        LocalDateTime now = LocalDateTime.now();
        DailyTrainTicket dailyTrainTicket = BeanUtil.copyProperties(req, DailyTrainTicket.class);
        if (ObjectUtil.isNull(dailyTrainTicket.getId())) {
            dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainTicket.setCreateTime(now);
            dailyTrainTicket.setUpdateTime(now);
            save(dailyTrainTicket);
        } else {
            dailyTrainTicket.setUpdateTime(now);
            updateById(dailyTrainTicket);
        }
    }

    public PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq req) {
        IPage<DailyTrainTicket> page = new Page<>(req.getPage(), req.getSize());

        IPage<DailyTrainTicket> list = lambdaQuery()
//            .eq(LoginMemberContext.getId() != null , DailyTrainTicket::getMemberId, LoginMemberContext.getId())
            .orderByDesc(DailyTrainTicket::getCreateTime)
            .page(page);

        if(list.getRecords().isEmpty()){
        return null;
        }

        List<DailyTrainTicketQueryResp> dailyTrainTicketQueryRespList = list.getRecords().stream().map(item -> {
            DailyTrainTicketQueryResp dailyTrainTicketQueryResp = BeanUtil.copyProperties(item, DailyTrainTicketQueryResp.class);
            return dailyTrainTicketQueryResp;
            }).toList();

        IPage<DailyTrainTicketQueryResp> dailyTrainTicketQueryRespPage = new Page<>(req.getPage(), req.getSize());
        dailyTrainTicketQueryRespPage.setTotal(list.getTotal());
        dailyTrainTicketQueryRespPage.setRecords(dailyTrainTicketQueryRespList);


        PageResp<DailyTrainTicketQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(dailyTrainTicketQueryRespPage.getTotal());
        pageResp.setList(dailyTrainTicketQueryRespPage.getRecords());
        return pageResp;
    }

    public void delete(Long id) {
        removeById(id);
    }

    public DailyTrainTicketQueryResp queryById(Long id) {

                DailyTrainTicket byId = getById(id);
        if(byId == null){
        return null;
        }

        return BeanUtil.copyProperties(byId, DailyTrainTicketQueryResp.class);
    }

    @Transactional
    public void genDaily(Date date, String trainCode) {
        LOG.info("开始生成日期【{}】车次【{}】的每日车票", DateUtil.format(date, "yyyy-MM-dd"), trainCode);
        // 查出所有的车站
        List<TrainStation> trainStationList = trainStationService.lambdaQuery()
                .eq(TrainStation::getTrainCode, trainCode)
                .orderByAsc(TrainStation::getIndex)
                .list();
        // 查出所有的座位个数和价格
        int YDZ_Count = dailyTrainSeatService.SeatCount(date, trainCode, SeatTypeEnum.YDZ);
        int EDZ_Count = dailyTrainSeatService.SeatCount(date, trainCode, SeatTypeEnum.EDZ);
        int RW_Count = dailyTrainSeatService.SeatCount(date, trainCode, SeatTypeEnum.RW);
        int YW_Count = dailyTrainSeatService.SeatCount(date, trainCode, SeatTypeEnum.YW);
        // 价格：里程数 * 座位类型系数 * 车次类型系数
        String trainType = trainService.lambdaQuery()
                .eq(Train::getCode, trainCode)
                .one().getType();
        BigDecimal trainTypePriceRate = EnumUtil.getFieldBy(TrainTypeEnum::getPriceRate, TrainTypeEnum::getCode, trainType);


        ArrayList<DailyTrainTicket> dailyTrainTickets = new ArrayList<>();
        for (int i = 0; i < trainStationList.size() - 1; i++) {
            TrainStation start = trainStationList.get(i);
            BigDecimal totalMileage = BigDecimal.ZERO;
            for (int j = i + 1; j < trainStationList.size(); j++) {
                LocalDateTime now = LocalDateTime.now();
                TrainStation end = trainStationList.get(j);
                DailyTrainTicket dailyTrainTicket = new DailyTrainTicket();
                // 计算里程数
                totalMileage = totalMileage.add(trainStationList.get(j).getKm());

                // 设置属性
                dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
                dailyTrainTicket.setDate(date);
                dailyTrainTicket.setTrainCode(trainCode);
                dailyTrainTicket.setStart(start.getName());
                dailyTrainTicket.setStartPinyin(start.getNamePinyin());
                dailyTrainTicket.setStartIndex(start.getIndex());
                dailyTrainTicket.setStartTime(start.getOutTime());
                dailyTrainTicket.setEnd(end.getName());
                dailyTrainTicket.setEndPinyin(end.getNamePinyin());
                dailyTrainTicket.setEndIndex(end.getIndex());
                dailyTrainTicket.setEndTime(end.getInTime());
                dailyTrainTicket.setCreateTime(now);
                dailyTrainTicket.setUpdateTime(now);

                dailyTrainTicket.setYdz(YDZ_Count);
                dailyTrainTicket.setYdzPrice(totalMileage
                        .multiply(trainTypePriceRate).multiply(SeatTypeEnum.YDZ.getPrice())
                        .setScale(2, RoundingMode.HALF_UP));
                dailyTrainTicket.setEdz(EDZ_Count);
                dailyTrainTicket.setEdzPrice(totalMileage
                        .multiply(trainTypePriceRate).multiply(SeatTypeEnum.EDZ.getPrice())
                        .setScale(2, RoundingMode.HALF_UP));
                dailyTrainTicket.setRw(RW_Count);
                dailyTrainTicket.setRwPrice(totalMileage
                        .multiply(trainTypePriceRate).multiply(SeatTypeEnum.RW.getPrice())
                        .setScale(2, RoundingMode.HALF_UP));
                dailyTrainTicket.setYw(YW_Count);
                dailyTrainTicket.setYwPrice(totalMileage
                        .multiply(trainTypePriceRate).multiply(SeatTypeEnum.YW.getPrice())
                        .setScale(2, RoundingMode.HALF_UP));

                dailyTrainTickets.add(dailyTrainTicket);
            }
        }
        // 删除每日车票数据
        lambdaUpdate()
                .eq(DailyTrainTicket::getDate, date)
                .eq(DailyTrainTicket::getTrainCode, trainCode)
                .remove();

        // 批量保存每日车票数据
        saveBatch(dailyTrainTickets, 500);

        LOG.info("✅结束生成日期【{}】车次【{}】的每日车票", DateUtil.format(date, "yyyy-MM-dd"), trainCode);
    }
}
