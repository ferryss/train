package com.szx.train.batch.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.sql.Blob;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@ApiModel(value="QrtzTriggers对象", description="")
public class QrtzTriggers implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "定时任务名称")
    @TableId(value = "SCHED_NAME", type = IdType.AUTO)
    private String schedName;

    @ApiModelProperty(value = "触发器名称")
    private String triggerName;

    @ApiModelProperty(value = "触发器组")
    private String triggerGroup;

    @ApiModelProperty(value = "job名称")
    private String jobName;

    @ApiModelProperty(value = "job组")
    private String jobGroup;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "下一次触发时间")
    private Long nextFireTime;

    @ApiModelProperty(value = "前一次触发时间")
    private Long prevFireTime;

    @ApiModelProperty(value = "等级")
    private Integer priority;

    @ApiModelProperty(value = "触发状态")
    private String triggerState;

    @ApiModelProperty(value = "触发类型")
    private String triggerType;

    @ApiModelProperty(value = "开始时间")
    private Long startTime;

    @ApiModelProperty(value = "结束时间")
    private Long endTime;

    @ApiModelProperty(value = "日程名称")
    private String calendarName;

    @ApiModelProperty(value = "未触发实例")
    private Integer misfireInstr;

    @ApiModelProperty(value = "job数据")
    private Blob jobData;


}
