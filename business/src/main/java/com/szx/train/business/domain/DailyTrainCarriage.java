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
 * 每日车厢
 * </p>
 *
 * @author Ferry
 * @since 2025-11-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("daily_train_carriage")
@Schema(name="DailyTrainCarriage对象", description="每日车厢")
public class DailyTrainCarriage implements Serializable {

    private static final long serialVersionUID = 1L;

    @SchemaProperty(name = "id")
    private Long id;

    @SchemaProperty(name = "日期")
    private LocalDate date;

    @SchemaProperty(name = "车次编号")
    private String trainCode;

    @SchemaProperty(name = "箱序")
    private Integer index;

    @SchemaProperty(name = "座位类型|枚举[SeatTypeEnum]")
    private String seatType;

    @SchemaProperty(name = "座位数")
    private Integer seatCount;

    @SchemaProperty(name = "排数")
    private Integer rowCount;

    @SchemaProperty(name = "列数")
    private Integer colCount;

    @SchemaProperty(name = "新增时间")
    private LocalDateTime createTime;

    @SchemaProperty(name = "修改时间")
    private LocalDateTime updateTime;


}
