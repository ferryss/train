package com.szx.train.business.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 确认订单
 * </p>
 *
 * @author Ferry
 * @since 2025-11-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("confirm_order")
@Schema(name="ConfirmOrder对象", description="确认订单")
public class ConfirmOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @SchemaProperty(name = "id")
    private Long id;

    @SchemaProperty(name = "会员id")
    private Long memberId;

    @SchemaProperty(name = "日期")
    private LocalDate date;

    @SchemaProperty(name = "车次编号")
    private String trainCode;

    @SchemaProperty(name = "出发站")
    private String start;

    @SchemaProperty(name = "到达站")
    private String end;

    @SchemaProperty(name = "余票ID")
    private Long dailyTrainTicketId;

    @SchemaProperty(name = "车票")
    private String tickets;

    @SchemaProperty(name = "订单状态|枚举[ConfirmOrderStatusEnum]")
    private String status;

    @SchemaProperty(name = "新增时间")
    private LocalDateTime createTime;

    @SchemaProperty(name = "修改时间")
    private LocalDateTime updateTime;


}
