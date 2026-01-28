package com.szx.train.business.enums;

import lombok.Getter;
import lombok.ToString;

@ToString
public enum RocketMQTopicEnum {

    CONFIRM_ORDER("CONFIRM_ORDER", "确认订单排队");

    @Getter
    private final String code;
    @Getter
    private final String desc;

    RocketMQTopicEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }


}
