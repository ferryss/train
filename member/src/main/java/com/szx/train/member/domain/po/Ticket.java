package com.szx.train.member.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * <p>
 * 车票
 * </p>
 *
 * @author Ferry
 * @since 2026-01-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ticket")
@Schema(name="Ticket对象", description="车票")
public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "会员id")
    private Long memberId;

    @Schema(description = "乘客id")
    private Long passengerId;

    @Schema(description = "乘客姓名")
    private String passengerName;

    @Schema(description = "日期")
    private LocalDate trainDate;

    @Schema(description = "车次编号")
    private String trainCode;

    @Schema(description = "箱序")
    private Integer carriageIndex;

    @Schema(description = "序号|01，02")
    private String seatRow;

    @Schema(description = "列号|枚举[SeatColEnum]")
    private String seatCol;

    @Schema(description = "出发站")
    private String startStation;

    @Schema(description = "出发时间")
    private LocalTime startTime;

    @Schema(description = "到达站")
    private String endStation;

    @Schema(description = "到站时间")
    private LocalTime endTime;

    @Schema(description = "座位类型|枚举[SeatTypeEnum]")
    private String seatType;

    @Schema(description = "新增时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;


}
