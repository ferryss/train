package com.szx.train.batch.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Blob;

/**
 * <p>
 * 
 * </p>
 *
 * @author Ferry
 * @since 2025-11-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("QRTZ_CALENDARS")
@Schema(name="QrtzCalendars对象", description="")
public class QrtzCalendars implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "定时任务名称")
    @TableId(value = "SCHED_NAME", type = IdType.AUTO)
    private String schedName;

    @Schema(name = "日程名称")
    private String calendarName;

    @Schema(name = "日程数据")
    private Blob calendar;


}
