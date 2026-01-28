package com.szx.train.business.mq;

import com.alibaba.fastjson.JSON;
import com.szx.train.business.dto.ConfirmOrderMQDto;
import com.szx.train.business.service.ConfirmOrderService;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

/**
 * @author ferry
 * @date 2026/1/27
 * @project train
 * @description
 */
@Service
@RocketMQMessageListener(consumerGroup = "default", topic = "CONFIRM_ORDER")
@RequiredArgsConstructor
public class ConfirmOrderConsumer implements RocketMQListener<MessageExt> {

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderConsumer.class);

    private final ConfirmOrderService confirmOrderService;
    @Override
    public void onMessage(MessageExt  messageExt) {
        byte[] body = messageExt.getBody();
        ConfirmOrderMQDto dto = JSON.parseObject(new String(body), ConfirmOrderMQDto.class);
        MDC.put("LOG_ID", dto.getLogId());
        LOG.info("收到消息: {}", new String(body));
        confirmOrderService.doConfirmOrder(dto);
    }
}
