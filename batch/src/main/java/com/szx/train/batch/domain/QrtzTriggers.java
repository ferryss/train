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
@TableName("QRTZ_TRIGGERS")
@Schema(name="QrtzTriggers对象", description="")
public class QrtzTriggers implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "定时任务名称")
    @TableId(value = "SCHED_NAME", type = IdType.AUTO)
    private String schedName;

    @Schema(name = "触发器名称")
    private String triggerName;

    @Schema(name = "触发器组")
    private String triggerGroup;

    @Schema(name = "job名称")
    private String jobName;

    @Schema(name = "job组")
    private String jobGroup;

    @Schema(name = "描述")
    private String description;

    @Schema(name = "下一次触发时间")
    private Long nextFireTime;

    @Schema(name = "前一次触发时间")
    private Long prevFireTime;

    @Schema(name = "等级")
    private Integer priority;

    @Schema(name = "触发状态")
    private String triggerState;

    @Schema(name = "触发类型")
    private String triggerType;

    @Schema(name = "开始时间")
    private Long startTime;

    @Schema(name = "结束时间")
    private Long endTime;

    @Schema(name = "日程名称")
    private String calendarName;

    @Schema(name = "未触发实例")
    private Integer misfireInstr;

    @Schema(name = "job数据")
    private Blob jobData;


}
