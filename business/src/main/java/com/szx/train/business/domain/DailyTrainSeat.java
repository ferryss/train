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
 * 每日座位
 * </p>
 *
 * @author Ferry
 * @since 2025-11-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("daily_train_seat")
@Schema(name="DailyTrainSeat对象", description="每日座位")
public class DailyTrainSeat implements Serializable {

    private static final long serialVersionUID = 1L;

    @SchemaProperty(name = "id")
    private Long id;

    @SchemaProperty(name = "日期")
    private LocalDate date;

    @SchemaProperty(name = "车次编号")
    private String trainCode;

    @SchemaProperty(name = "箱序")
    private Integer carriageIndex;

    @SchemaProperty(name = "排号|01, 02")
    private String row;

    @SchemaProperty(name = "列号|枚举[SeatColEnum]")
    private String col;

    @SchemaProperty(name = "座位类型|枚举[SeatTypeEnum]")
    private String seatType;

    @SchemaProperty(name = "同车箱座序")
    private Integer carriageSeatIndex;

    @SchemaProperty(name = "售卖情况|将经过的车站用01拼接，0表示可卖，1表示已卖")
    private String sell;

    @SchemaProperty(name = "新增时间")
    private LocalDateTime createTime;

    @SchemaProperty(name = "修改时间")
    private LocalDateTime updateTime;


}
