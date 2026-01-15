package com.szx.train.member.domain.dto;

import com.szx.train.common.req.PageReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * <p>
 * 车票
 * </p>
 *
 * @author Ferry
 * @since 2026-01-11
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketQueryReq extends PageReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会员id
     */
    private Long memberId;

    /**
     * 乘客id
     */
    private Long passengerId;

    /**
     * 乘客姓名
     */
    private String passengerName;

    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 车次编号
     */
    private String trainCode;

    /**
     * 箱序
     */
    private Integer carriageIndex;

    /**
     * 序号|01，02
     */
    private String row;

    /**
     * 列号|枚举[SeatColEnum]
     */
    private String col;

    /**
     * 出发站
     */
    private String start;

    /**
     * 出发时间
     */
    private LocalTime startTime;

    /**
     * 到达站
     */
    private String end;

    /**
     * 到站时间
     */
    private LocalTime endTime;

    /**
     * 座位类型|枚举[SeatTypeEnum]
     */
    private String seatType;

}
