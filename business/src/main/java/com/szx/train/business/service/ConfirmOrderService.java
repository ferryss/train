package com.szx.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szx.train.business.domain.ConfirmOrder;
import com.szx.train.business.domain.DailyTrain;
import com.szx.train.business.domain.DailyTrainTicket;
import com.szx.train.business.enums.ConfirmOrderStatusEnum;
import com.szx.train.business.mapper.ConfirmOrderMapper;
import com.szx.train.business.req.ConfirmOrderDoReq;
import com.szx.train.business.req.ConfirmOrderQueryReq;
import com.szx.train.business.req.ConfirmOrderSaveReq;
import com.szx.train.business.req.ConfirmOrderTicketReq;
import com.szx.train.business.resp.ConfirmOrderQueryResp;
import com.szx.train.common.context.LoginMemberContext;
import com.szx.train.common.resp.PageResp;
import com.szx.train.common.util.SnowUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfirmOrderService extends ServiceImpl<ConfirmOrderMapper, ConfirmOrder> {

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

    private final DailyTrainTicketService dailyTrainTicketService;
    private final DailyTrainService dailyTrainService;

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

        boolean save = save(confirmOrder);

//        // 拿到余票数量做扣减
//        Integer ydzCount = trainTicket.getYdz();
//        Integer edzCount = trainTicket.getEdz();
//        Integer ywCount = trainTicket.getYw();
//        Integer rwCount = trainTicket.getYw();
//        Integer deductYdzCount = ydzCount;
//        Integer deductEdzCount = edzCount;
//        Integer deductYwCount = ywCount;
//        Integer deductRwCount = rwCount;
//
//        String code = SeatTypeEnum.YDZ.getCode();
//        for (ConfirmOrderTicketReq ticket : tickets) {
//            String seatTypeCode = ticket.getSeatTypeCode();
//
//            if(SeatTypeEnum.YDZ.getCode().equals(seatTypeCode)){
//                deductYdzCount--;
//            }else if(SeatTypeEnum.EDZ.getCode().equals(seatTypeCode)){
//                deductEdzCount--;
//            } else if (SeatTypeEnum.RW.getCode().equals(seatTypeCode)) {
//                deductRwCount--;
//            } else if (SeatTypeEnum.YW.getCode().equals(seatTypeCode)) {
//                deductYwCount--;
//            }
//        }
//
//        if(deductYdzCount < 0 || deductEdzCount < 0 || deductYwCount < 0 || deductRwCount < 0){
//            LOG.info("余票不足");
//
//            return;
//        }

        // 遍历车厢确定座位


    }
}
