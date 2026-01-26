package com.szx.train.common.exception;

public enum BusinessExceptionEnum {

    MEMBER_MOBILE_BLANK("手机号为空"),

    MEMBER_MOBILE_EXIST("手机号已注册"),
    MEMBER_MOBILE_NOT_EXIST("请先获取短信验证码"),
    MEMBER_MOBILE_CODE_ERROR("短信验证码错误"),

    MEMBER_PASSENGER_COUNT_EXCEEDING("最多可以存在50个乘车人"),

    BUSINESS_STATION_NAME_UNIQUE_ERROR("车站已存在"),
    BUSINESS_TRAIN_CODE_UNIQUE_ERROR("车次编号已存在"),
    BUSINESS_TRAIN_STATION_INDEX_UNIQUE_ERROR("同车次站序已存在"),
    BUSINESS_TRAIN_STATION_NAME_UNIQUE_ERROR("同车次站名已存在"),
    BUSINESS_TRAIN_CARRIAGE_INDEX_UNIQUE_ERROR("同车次厢号已存在"),
    BUSINESS_TICKET_INSUFFICIENT("余票不足"),
    CONFIRM_ORDER_SAVE_ERROR("保存购票订单失败"),
    CONFIRM_ORDER_LOCK_FAIL("当前购票人数过多,请稍后重试"),
    CONFIRM_ORDER_FLOE_ERROR("当前购票人数过多,请稍后重试"),
    CONFIRM_ORDER_SK_TOKEN_FAIL("票已卖光");

    private String desc;

    BusinessExceptionEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "BusinessExceptionEnum{" +
                "desc='" + desc + '\'' +
                "} " + super.toString();
    }
}
