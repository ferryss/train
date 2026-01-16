package com.szx.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
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
import com.szx.train.business.feign.MemberFeign;
import com.szx.train.business.mapper.ConfirmOrderMapper;
import com.szx.train.business.req.ConfirmOrderDoReq;
import com.szx.train.business.req.ConfirmOrderQueryReq;
import com.szx.train.business.req.ConfirmOrderSaveReq;
import com.szx.train.business.req.ConfirmOrderTicketReq;
import com.szx.train.business.resp.ConfirmOrderQueryResp;
import com.szx.train.common.context.LoginMemberContext;
import com.szx.train.common.exception.BusinessException;
import com.szx.train.common.exception.BusinessExceptionEnum;
import com.szx.train.common.req.TicketReq;
import com.szx.train.common.resp.CommonResp;
import com.szx.train.common.resp.PageResp;
import com.szx.train.common.util.SnowUtil;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
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
    private final MemberFeign memberFeign;

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
        Long memberId = req.getMemberId();

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

        // 最终选票列表
        ArrayList<DailyTrainSeat> finalSeatList = new ArrayList<>();

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
            getSeat(finalSeatList,
                    date,
                    trainCode,
                    ticketReq0.getSeatTypeCode(),
                    ticketReq0.getSeat().split("")[0],
                    seatOffsetList,
                    trainTicket.getStartIndex(),
                    trainTicket.getEndIndex());

        }else{
            LOG.info("该购票无选座");
            for(ConfirmOrderTicketReq ticket : tickets){
                getSeat(finalSeatList,
                        date,
                        trainCode,
                        ticket.getSeatTypeCode(),
                        null,
                        null,
                        trainTicket.getStartIndex(),
                        trainTicket.getEndIndex());
            }
        }

        LOG.info("最终选座列表：{}", finalSeatList);

        // 修改数据库
        // 1.每日座位表销售情况修改
        // 2.对应余票剩余数量修改
        // 3.用户的车票新增
        // 4.确认订单表状态修改
        // 也可以单独创建一个类来调用此方法

        List<DailyTrainTicket> ticketList = dailyTrainTicketService.lambdaQuery()
                .eq(date != null, DailyTrainTicket::getDate, date)
                .eq(StrUtil.isNotBlank(trainCode), DailyTrainTicket::getTrainCode, trainCode)
                .list();
        ConfirmOrderService proxy = (ConfirmOrderService) AopContext.currentProxy();
        try {
            proxy.afterDoConfirmOrder(finalSeatList, ticketList, tickets, start, end, confirmOrder);
        } catch (Exception e) {
            LOG.error("保存购票信息失败", e);
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SAVE_ERROR);
        }

    }

    @GlobalTransactional
    public void afterDoConfirmOrder(ArrayList<DailyTrainSeat> finalSeatList, List<DailyTrainTicket> ticketList,
                                    List<ConfirmOrderTicketReq> tickets,
                                    String start, String end, ConfirmOrder confirmOrder) throws Exception {
        LOG.info("seata全局事务ID为 {}", RootContext.getXID());
        for (int i = 0; i < finalSeatList.size(); i++){
            DailyTrainSeat finalSeat = finalSeatList.get(i);
            DateTime now = DateTime.now();
            // 每日座位表销售情况修改
            dailyTrainSeatService.lambdaUpdate()
                    .set(StrUtil.isNotBlank(finalSeat.getSell()), DailyTrainSeat::getSell, finalSeat.getSell())
                    .set(StrUtil.isNotBlank(finalSeat.getSell()), DailyTrainSeat::getUpdateTime, now)
                    .eq(finalSeat.getId() != null, DailyTrainSeat::getId, finalSeat.getId())
                    .update();

            String sell = finalSeat.getSell();
            String seatType = finalSeat.getSeatType();
            int[] prefixIndex = getPrefixIndex(sell);

            LocalTime startTime = null;
            LocalTime endTime = null;

            // 每日余票数量修改
            for(DailyTrainTicket ticket : ticketList){
                if(ticket.getStart().equals(start)){
                    startTime = ticket.getStartTime();
                }
                if(ticket.getEnd().equals(end)){
                    endTime = ticket.getEndTime();
                }

                Integer startIndex = ticket.getStartIndex();
                Integer endIndex = ticket.getEndIndex();
                if(prefixIndex[endIndex] - prefixIndex[startIndex] > 0){ //为0说明该区间的票不受影响
                    switch (seatType) {
                        case "1":
                            dailyTrainTicketService.lambdaUpdate()
                                    .set(DailyTrainTicket::getYdz, ticket.getYdz()-1)
                                    .eq(ticket.getId() != null, DailyTrainTicket::getId, ticket.getId())
                                    .update();
                            break;

                        case "2":
                            dailyTrainTicketService.lambdaUpdate()
                                    .set(DailyTrainTicket::getEdz, ticket.getEdz()-1)
                                    .eq(ticket.getId() != null, DailyTrainTicket::getId, ticket.getId())
                                    .update();
                            break;
                    }
                }
            }

            // 乘车人的信息
            ConfirmOrderTicketReq ticket = tickets.get(i);

            // 用户的车票新增
            TicketReq ticketReq = new TicketReq();
            ticketReq.setMemberId(LoginMemberContext.getId());
            ticketReq.setPassengerId(ticket.getPassengerId());
            ticketReq.setPassengerName(ticket.getPassengerName());
            ticketReq.setTrainDate(finalSeat.getDate());
            ticketReq.setTrainCode(finalSeat.getTrainCode());
            ticketReq.setCarriageIndex(finalSeat.getCarriageIndex());
            ticketReq.setSeatRow(finalSeat.getRow());
            ticketReq.setSeatCol(finalSeat.getCol());
            ticketReq.setStartStation(start);
            ticketReq.setStartTime(startTime);
            ticketReq.setEndStation(end);
            ticketReq.setEndTime(endTime);
            ticketReq.setSeatType(finalSeat.getSeatType());
            CommonResp<Object> objectCommonResp = memberFeign.saveTicket(ticketReq);
            LOG.info("用户车票新增：{}", ticketReq);
            LOG.info("用户车票新增结果(调用member)：{}", objectCommonResp);


            // 确认订单表状态修改
            lambdaUpdate()
                    .set(ConfirmOrder::getStatus, ConfirmOrderStatusEnum.SUCCESS.getCode())
                    .eq(ConfirmOrder::getId, confirmOrder.getId())
                    .update();
//            if(1 == 1){
//                throw new Exception("测试异常");
//            }
        }

    }

    private int[] getPrefixIndex(String sell){
        int n = sell.length();

        int[] bitmap = new int[n];
        for (int i = 0; i < n; i++) {
            char c = sell.charAt(i);
            if (c == '0') {
                bitmap[i] = 0;
            } else {
                bitmap[i] = 1;
            }
        }
        LOG.info("销售情况：{}", Arrays.toString(bitmap));

        int[] prefix = new int[n + 1];
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + bitmap[i];
        }
        LOG.info("站序前缀和：{}", Arrays.toString(prefix));

        return prefix;
    }


    private void getSeat(ArrayList<DailyTrainSeat> finalSeatList,
                         Date date, String trainCode, String SeatType,
                         String colType, ArrayList<Integer> seatOffsetList,
                         Integer startIndex, Integer endIndex){

        // offset临时存储座位列表
        ArrayList<DailyTrainSeat> tempSeatList = new ArrayList<>();

        List<DailyTrainCarriage> dailyTrainCarriageList = dailyTrainCarriageService.lambdaQuery()
                .eq(DailyTrainCarriage::getDate, date)
                .eq(DailyTrainCarriage::getTrainCode, trainCode)
                .eq(DailyTrainCarriage::getSeatType, SeatType)
                .orderByAsc(DailyTrainCarriage::getIndex)
                .list();
        LOG.info("符合要求车厢列表数量：{}", dailyTrainCarriageList.size());

        for(DailyTrainCarriage dailyTrainCarriage : dailyTrainCarriageList){

            List<DailyTrainSeat> dailyTrainSeatList = dailyTrainSeatService.lambdaQuery()
                    .eq(DailyTrainSeat::getDate, date)
                    .eq(DailyTrainSeat::getTrainCode, trainCode)
                    .eq(DailyTrainSeat::getCarriageIndex, dailyTrainCarriage.getIndex())
                    .orderByAsc(DailyTrainSeat::getCarriageSeatIndex)
                    .list();
            LOG.info("车厢 {} 符合要求座位数量：{}", dailyTrainCarriage.getIndex(),
                    dailyTrainSeatList.size());


            for(DailyTrainSeat dailyTrainSeat : dailyTrainSeatList){

                Integer curSeatIndex = dailyTrainSeat.getCarriageSeatIndex();
                String col = dailyTrainSeat.getCol();

                // 座位是否已预选
                boolean isAlreadyChoose = false;
                // 判断座位是否预选定
                for(DailyTrainSeat finalSeat : finalSeatList){
                    if(curSeatIndex.equals(finalSeat.getCarriageSeatIndex())){
                        LOG.info("座位 {} 已被预选", curSeatIndex);
                        isAlreadyChoose = true;
                        break;
                    }
                }
                if(isAlreadyChoose){
                    continue;
                }

                // 判断列号是否符合
                if(StrUtil.isBlank(colType)){
                    LOG.info("座位 {} 无列号要求", curSeatIndex);
                }else {
                    if(!col.equals(colType)){
                        LOG.info("座位 {} 列号不符合要求, 要求列号: {}, 目标列号: {}", curSeatIndex, colType, col);
                        continue;
                    }
                }

                // 判断座位是否可卖
                boolean isSell = calSell(dailyTrainSeat, startIndex, endIndex);
                if(!isSell){
                    LOG.info("座位 {} 已被卖", curSeatIndex);
                    continue;
                }else {
                    LOG.info("座位 {} 被选行号 {}, 列号 {}", curSeatIndex,
                            dailyTrainSeat.getRow(), dailyTrainSeat.getCol());
                    tempSeatList.add(dailyTrainSeat);
                }

                boolean isContinueCal = true;

                // 判断偏移列表是否空
                if(CollUtil.isNotEmpty(seatOffsetList)){
                    LOG.info("该座位有偏移列表 {}", seatOffsetList );

                    for (int i = 1; i < seatOffsetList.size(); i++) {

                        int nextIndex =  curSeatIndex + seatOffsetList.get(i) - 1;
                        if(nextIndex >= dailyTrainSeatList.size()){
                            LOG.info("座位偏移超出范围");
                            isContinueCal = false;
                            break;
                        }

                        DailyTrainSeat offsetTrainSeat = dailyTrainSeatList.get(nextIndex);
                        Integer offsetSeatIndex = offsetTrainSeat.getCarriageSeatIndex();

                        boolean isOffsetSell = calSell(offsetTrainSeat, startIndex, endIndex);
                        if(!isOffsetSell){
                            LOG.info("偏移座位 {} 已被卖", offsetSeatIndex);
                            isContinueCal = false;
                            break;
                        }else {
                            LOG.info("座位 {} 被选行号 {}, 列号 {}", offsetSeatIndex,
                                    offsetTrainSeat.getRow(), offsetTrainSeat.getCol());
                            tempSeatList.add(offsetTrainSeat);
                        }
                    }
                }

                if(!isContinueCal){
                    LOG.info("该座位 {} 的偏移列表无法继续计算, 开启下个座位计算", curSeatIndex);
                    tempSeatList.clear();
                    continue;
                }

                // 保存座位信息
                finalSeatList.addAll(tempSeatList);

                return;
            }
        }
    }

    private boolean calSell(DailyTrainSeat dailyTrainSeat, int startIndex, int endIndex){

        String sell = dailyTrainSeat.getSell();
        int length = sell.length();
        String sellPart = sell.substring(startIndex, endIndex);
        if(Integer.parseInt(sellPart) > 0){
            // 不可卖
            return false;
        }else{
            // 可卖
            sellPart = sellPart.replace("0", "1");
            sellPart = StrUtil.fillBefore(sellPart, '0', endIndex);
            sellPart = StrUtil.fillAfter(sellPart, '0', length);

            int newSell = NumberUtil.binaryToInt(sellPart) | NumberUtil.binaryToInt(sell);
            String newSellStr = NumberUtil.getBinaryStr(newSell);
            newSellStr = StrUtil.fillBefore(newSellStr, '0', length);

            LOG.info("座位{}被选中， 原售票信息:{}, 车站区间: {}~{}, 即: {}, 最终售票信息: {}",
                    dailyTrainSeat.getCarriageSeatIndex(), sell, startIndex, endIndex,
                    sellPart, newSellStr);
            dailyTrainSeat.setSell(newSellStr);
            return true;
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
