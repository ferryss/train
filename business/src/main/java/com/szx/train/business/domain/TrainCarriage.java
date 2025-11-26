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
import java.time.LocalDateTime;

/**
 * <p>
 * 火车车厢
 * </p>
 *
 * @author Ferry
 * @since 2025-11-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("train_carriage")
@Schema(name="TrainCarriage对象", description="火车车厢")
public class TrainCarriage implements Serializable {

    private static final long serialVersionUID = 1L;

    @SchemaProperty(name = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @SchemaProperty(name = "车次编号")
    private String trainCode;

    @SchemaProperty(name = "厢号")
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
