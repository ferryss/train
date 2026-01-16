package com.szx.train.common.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Date;

/**
 * <p>
 * 车票
 * </p>
 *
 * @author Ferry
 * @since 2026-01-11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "【会员id】不能为空")
    /**
     * 会员id
     */
    private Long memberId;

    @NotNull(message = "【乘客id】不能为空")
    /**
     * 乘客id
     */
    private Long passengerId;

    @NotNull(message = "【乘客姓名】不能为空")
    /**
     * 乘客姓名
     */
    private String passengerName;

    @NotNull(message = "【日期】不能为空")
    /**
     * 日期
     */
    private Date trainDate;

    @NotBlank(message = "【车次编号】不能为空")
    /**
     * 车次编号
     */
    private String trainCode;

    @NotNull(message = "【箱序】不能为空")
    /**
     * 箱序
     */
    private Integer carriageIndex;

    @NotBlank(message = "【座位号】不能为空")
    /**
     * 序号|01，02
     */
    private String seatRow;

    @NotBlank(message = "【列号】不能为空")
    /**
     * 列号|枚举[SeatColEnum]
     */
    private String seatCol;

    @NotBlank(message = "【出发站】不能为空")
    /**
     * 出发站
     */
    private String startStation;

    @NotNull(message = "【出发时间】不能为空")
    /**
     * 出发时间
     */
    private LocalTime startTime;

    @NotBlank(message = "【到达站】不能为空")
    /**
     * 到达站
     */
    private String endStation;

    @NotNull(message = "【到站时间】不能为空")
    /**
     * 到站时间
     */
    private LocalTime endTime;

    @NotBlank(message = "【座位类型】不能为空")
    /**
     * 座位类型|枚举[SeatTypeEnum]
     */
    private String seatType;

}
