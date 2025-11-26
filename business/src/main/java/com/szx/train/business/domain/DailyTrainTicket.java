package com.szx.train.business.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * <p>
 * 余票信息
 * </p>
 *
 * @author Ferry
 * @since 2025-11-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("daily_train_ticket")
@Schema(name="DailyTrainTicket对象", description="余票信息")
public class DailyTrainTicket implements Serializable {

    private static final long serialVersionUID = 1L;

    @SchemaProperty(name = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @SchemaProperty(name = "日期")
    private LocalDate date;

    @SchemaProperty(name = "车次编号")
    private String trainCode;

    @SchemaProperty(name = "出发站")
    private String start;

    @SchemaProperty(name = "出发站拼音")
    private String startPinyin;

    @SchemaProperty(name = "出发时间")
    private LocalTime startTime;

    @SchemaProperty(name = "出发站序|本站是整个车次的第几站")
    private Integer startIndex;

    @SchemaProperty(name = "到达站")
    private String end;

    @SchemaProperty(name = "到达站拼音")
    private String endPinyin;

    @SchemaProperty(name = "到站时间")
    private LocalTime endTime;

    @SchemaProperty(name = "到站站序|本站是整个车次的第几站")
    private Integer endIndex;

    @SchemaProperty(name = "一等座余票")
    private Integer ydz;

    @SchemaProperty(name = "一等座票价")
    private BigDecimal ydzPrice;

    @SchemaProperty(name = "二等座余票")
    private Integer edz;

    @SchemaProperty(name = "二等座票价")
    private BigDecimal edzPrice;

    @SchemaProperty(name = "软卧余票")
    private Integer rw;

    @SchemaProperty(name = "软卧票价")
    private BigDecimal rwPrice;

    @SchemaProperty(name = "硬卧余票")
    private Integer yw;

    @SchemaProperty(name = "硬卧票价")
    private BigDecimal ywPrice;

    @SchemaProperty(name = "新增时间")
    private LocalDateTime createTime;

    @SchemaProperty(name = "修改时间")
    private LocalDateTime updateTime;


}
