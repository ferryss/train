package com.szx.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.fastjson.JSON;
import com.szx.train.business.domain.ConfirmOrder;
import com.szx.train.business.dto.ConfirmOrderMQDto;
import com.szx.train.business.enums.ConfirmOrderStatusEnum;
import com.szx.train.business.enums.RocketMQTopicEnum;
import com.szx.train.business.req.ConfirmOrderDoReq;
import com.szx.train.business.req.ConfirmOrderTicketReq;
import com.szx.train.common.context.LoginMemberContext;
import com.szx.train.common.exception.BusinessException;
import com.szx.train.common.exception.BusinessExceptionEnum;
import com.szx.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ferry
 * @date 2026/1/27
 * @project train
 * @description
 */
@Service
@RequiredArgsConstructor
public class BeforeConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(BeforeConfirmOrderService.class);

    private final SkTokenService skTokenService;
    private final ConfirmOrderService confirmOrderService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @SentinelResource(value = "beforeDoConfirmOrder", blockHandler = "beforeDoConfirmOrderBlock")
    public void beforeDoConfirmOrder(ConfirmOrderDoReq req){
        req.setMemberId(LoginMemberContext.getId());
        // 校验验证码

        // 校验令牌余量
        boolean validSkToken = skTokenService.validSkToken(req.getDate(), req.getTrainCode());
        if(validSkToken){
            LOG.info("令牌余量校验成功");
        }else{
            LOG.info("令牌余量校验失败");
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
        }

        List<ConfirmOrderTicketReq> tickets = req.getTickets();

        // （同乘客不可在 同车次 同时段 重复下单，）先不校验影响测试

        // 保存确认订单
        LocalDateTime nowTime  = LocalDateTime.now();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);
        confirmOrder.setId(SnowUtil.getSnowflakeNextId());
        confirmOrder.setMemberId(req.getMemberId());
        confirmOrder.setTickets(JSONUtil.toJsonStr(tickets));
        confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
        confirmOrder.setCreateTime(nowTime);
        confirmOrder.setUpdateTime(nowTime);

        confirmOrderService.save(confirmOrder);

        // 通过mq发送消息给购票服务
        ConfirmOrderMQDto dto = new ConfirmOrderMQDto();
        dto.setLogId(MDC.get("LOG_ID"));
        dto.setDate(req.getDate());
        dto.setTrainCode(req.getTrainCode());
        String reqJson = JSON.toJSONString(dto);
        LOG.info("排队购票，发送mq开始, 消息: {}", reqJson);
        rocketMQTemplate.convertAndSend(RocketMQTopicEnum.CONFIRM_ORDER.getCode(), reqJson);
        LOG.info("排队购票，发送mq结束, 消息: {}", reqJson);
    }
}
