package com.szx.train.business.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 座位
 * </p>
 *
 * @author Ferry
 * @since 2025-11-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("train_seat")
@Schema(name="TrainSeat对象", description="座位")
public class TrainSeat implements Serializable {

    private static final long serialVersionUID = 1L;

    @SchemaProperty(name = "id")
    private Long id;

    @SchemaProperty(name = "车次编号")
    private String trainCode;

    @SchemaProperty(name = "厢序")
    private Integer carriageIndex;

    @SchemaProperty(name = "排号|01, 02")
    private String row;

    @SchemaProperty(name = "列号|枚举[SeatColEnum]")
    private String col;

    @SchemaProperty(name = "座位类型|枚举[SeatTypeEnum]")
    private String seatType;

    @SchemaProperty(name = "同车厢座序")
    private Integer carriageSeatIndex;

    @SchemaProperty(name = "新增时间")
    private LocalDateTime createTime;

    @SchemaProperty(name = "修改时间")
    private LocalDateTime updateTime;


}
