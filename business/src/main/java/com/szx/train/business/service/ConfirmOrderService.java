package com.szx.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szx.train.business.domain.*;
import com.szx.train.business.enums.ConfirmOrderStatusEnum;
import com.szx.train.business.enums.SeatColEnum;
import com.szx.train.business.enums.SeatTypeEnum;
import com.szx.train.business.mapper.ConfirmOrderMapper;
import com.szx.train.business.req.ConfirmOrderDoReq;
import com.szx.train.business.req.ConfirmOrderQueryReq;
import com.szx.train.business.req.ConfirmOrderSaveReq;
import com.szx.train.business.req.ConfirmOrderTicketReq;
import com.szx.train.business.resp.ConfirmOrderQueryResp;
import com.szx.train.common.context.LoginMemberContext;
import com.szx.train.common.exception.BusinessException;
import com.szx.train.common.exception.BusinessExceptionEnum;
import com.szx.train.common.resp.PageResp;
import com.szx.train.common.util.SnowUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfirmOrderService extends ServiceImpl<ConfirmOrderMapper, ConfirmOrder> {

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

    private final DailyTrainTicketService dailyTrainTicketService;
    private final DailyTrainService dailyTrainService;
    private final DailyTrainCarriageService dailyTrainCarriageService;
    private final DailyTrainSeatService dailyTrainSeatService;

    public void saveConfirmOrder(ConfirmOrderSaveReq req) {
        LocalDateTime now = LocalDateTime.now();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);
        if (ObjectUtil.isNull(confirmOrder.getId())) {
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            save(confirmOrder);
        } else {
            confirmOrder.setUpdateTime(now);
            updateById(confirmOrder);
        }
    }

    public PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req) {
        IPage<ConfirmOrder> page = new Page<>(req.getPage(), req.getSize());

        IPage<ConfirmOrder> list = lambdaQuery()
            .orderByDesc(ConfirmOrder::getDate, ConfirmOrder::getUpdateTime)
            .page(page);

        if(list.getRecords().isEmpty()){
        return null;
        }

        List<ConfirmOrderQueryResp> confirmOrderQueryRespList = list.getRecords().stream().map(item -> {
            ConfirmOrderQueryResp confirmOrderQueryResp = BeanUtil.copyProperties(item, ConfirmOrderQueryResp.class);
            return confirmOrderQueryResp;
            }).toList();

        IPage<ConfirmOrderQueryResp> confirmOrderQueryRespPage = new Page<>(req.getPage(), req.getSize());
        confirmOrderQueryRespPage.setTotal(list.getTotal());
        confirmOrderQueryRespPage.setRecords(confirmOrderQueryRespList);


        PageResp<ConfirmOrderQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(confirmOrderQueryRespPage.getTotal());
        pageResp.setList(confirmOrderQueryRespPage.getRecords());
        return pageResp;
    }

    public void delete(Long id) {
        removeById(id);
    }

    public ConfirmOrderQueryResp queryById(Long id) {

                ConfirmOrder byId = getById(id);
        if(byId == null){
        return null;
        }

        return BeanUtil.copyProperties(byId, ConfirmOrderQueryResp.class);
    }

    public void doConfirmOrder(ConfirmOrderDoReq  req) {


        Date date = req.getDate();
        String trainCode = req.getTrainCode();
        String start = req.getStart();
        String end = req.getEnd();
        List<ConfirmOrderTicketReq> tickets = req.getTickets();

        // 校验
        // 车次是否在有效时间内
        DateTime now = DateTime.now();
        DateTime offset = DateUtil.offset(now, DateField.DAY_OF_YEAR, 14);
        boolean in = DateUtil.isIn(date, now, offset);
        if(!in){
            LOG.info("车次不在有效时间");
            return;
        }
        // tickets条数>0
        if(tickets.isEmpty()){
            LOG.info("tickets条数必须大于0");
            return;
        }
        // 车次是否存在
        DailyTrain train = dailyTrainService.lambdaQuery()
                .eq(date != null, DailyTrain::getDate, date)
                .eq(StrUtil.isNotBlank(trainCode), DailyTrain::getCode, trainCode)
                .one();
        if(train == null){
            LOG.info("车次不存在");
            return;
        }

        // 余票是否存在
        DailyTrainTicket trainTicket = dailyTrainTicketService.lambdaQuery()
                .eq(date != null, DailyTrainTicket::getDate, date)
                .eq(StrUtil.isNotBlank(trainCode), DailyTrainTicket::getTrainCode, trainCode)
                .eq(StrUtil.isNotBlank(start), DailyTrainTicket::getStart, start)
                .eq(StrUtil.isNotBlank(end), DailyTrainTicket::getEnd, end)
                .one();

        if(trainTicket == null){
            LOG.info("余票不存在");
            return;
        }

        // （同乘客不可在 同车次 同时段 重复下单，）先不校验影响测试

        // 保存确认订单
        LocalDateTime nowTime  = LocalDateTime.now();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);
        confirmOrder.setId(SnowUtil.getSnowflakeNextId());
        confirmOrder.setMemberId(LoginMemberContext.getId());
        confirmOrder.setTickets(JSONUtil.toJsonStr(tickets));
        confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
        confirmOrder.setCreateTime(nowTime);
        confirmOrder.setUpdateTime(nowTime);

        save(confirmOrder);

        // 拿到余票数量做预扣减
        reduceCount(tickets, trainTicket);

        // 判断是否选座
        ConfirmOrderTicketReq ticketReq0 = tickets.get(0);
        if(StrUtil.isNotBlank(ticketReq0.getSeat())){
            LOG.info("该购票有选座");
            List<SeatColEnum> colsByType = SeatColEnum.getColsByType(ticketReq0.getSeatTypeCode());
            // 选座列表 {A1, C1, D1, F1, A2, C2, D2, F2}
            ArrayList<String> seatList = new ArrayList<>();
            for (int i = 1; i <= 2; i++) {
                for(SeatColEnum col : colsByType){
                    seatList.add(col.getCode() + i);
                }
            }
            LOG.info("座位列表：{}", seatList);

            ArrayList<Integer> absoluteSeatIndexList = new ArrayList<>();
            ArrayList<Integer> seatOffsetList = new ArrayList<>();
            // 获得绝对座位索引
            for(ConfirmOrderTicketReq ticket : tickets){
                int absoluteIndex = seatList.indexOf(ticket.getSeat());
                absoluteSeatIndexList.add(absoluteIndex);
            }
            LOG.info("绝对座位索引：{}", absoluteSeatIndexList);
            // 获得相对座位索引
            for (Integer absoluteIndex : absoluteSeatIndexList) {
                int seatIndex = absoluteIndex - absoluteSeatIndexList.get(0);
                seatOffsetList.add(seatIndex);
            }
            LOG.info("相对座位索引：{}", seatOffsetList);
            getSeat(date,
                    trainCode,
                    ticketReq0.getSeatTypeCode(),
                    ticketReq0.getSeat().split("")[0],
                    seatOffsetList);

        }else{
            LOG.info("该购票无选座");
            for(ConfirmOrderTicketReq ticket : tickets){
                getSeat(date,
                        trainCode,
                        ticket.getSeatTypeCode(),
                        null,
                        null);
            }
        }

        // 遍历车厢确定座位

    }


    private void getSeat(Date date, String trainCode, String SeatType,
                                 String colType, ArrayList<Integer> seatOffsetList){

        List<DailyTrainCarriage> dailyTrainCarriageList = dailyTrainCarriageService.lambdaQuery()
                .eq(DailyTrainCarriage::getDate, date)
                .eq(DailyTrainCarriage::getTrainCode, trainCode)
                .eq(DailyTrainCarriage::getSeatType, SeatType)
                .list();
        LOG.info("符合要求车厢列表数量：{}", dailyTrainCarriageList.size());

        for(DailyTrainCarriage dailyTrainCarriage : dailyTrainCarriageList){
            List<DailyTrainSeat> dailyTrainSeatList = dailyTrainSeatService.lambdaQuery()
                    .eq(DailyTrainSeat::getDate, date)
                    .eq(DailyTrainSeat::getTrainCode, trainCode)
                    .eq(DailyTrainSeat::getCarriageIndex, dailyTrainCarriage.getIndex())
                    .list();
            LOG.info("车厢 {} 符合要求座位数量：{}", dailyTrainCarriage.getIndex(),
                    dailyTrainSeatList.size());
        }

    }

    private void reduceCount(List<ConfirmOrderTicketReq> tickets, DailyTrainTicket trainTicket) {
        for(ConfirmOrderTicketReq ticket : tickets){
            SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getCode, ticket.getSeatTypeCode());

            switch (seatTypeEnum){
                case YDZ -> {
                    int reduceCount = trainTicket.getYdz() - 1;
                    if(reduceCount < 0){
                        throw new BusinessException(BusinessExceptionEnum.BUSINESS_TICKET_INSUFFICIENT);
                    }
                    trainTicket.setYdz(reduceCount);
                }
                case EDZ -> {
                    int reduceCount = trainTicket.getEdz() - 1;
                    if(reduceCount < 0){
                        throw new BusinessException(BusinessExceptionEnum.BUSINESS_TICKET_INSUFFICIENT);
                    }
                    trainTicket.setEdz(reduceCount);
                }
                case RW -> {
                    int reduceCount = trainTicket.getRw() - 1;
                    if(reduceCount < 0){
                        throw new BusinessException(BusinessExceptionEnum.BUSINESS_TICKET_INSUFFICIENT);
                    }
                    trainTicket.setRw(reduceCount);
                }
                case YW -> {
                    int reduceCount = trainTicket.getYw() - 1;
                    if(reduceCount < 0){
                        throw new BusinessException(BusinessExceptionEnum.BUSINESS_TICKET_INSUFFICIENT);
                    }
                    trainTicket.setYw(reduceCount);
                }
            }
        }
    }
}
