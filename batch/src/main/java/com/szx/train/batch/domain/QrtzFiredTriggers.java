package com.szx.train.batch.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
@TableName("QRTZ_FIRED_TRIGGERS")
@Schema(name="QrtzFiredTriggers对象", description="")
public class QrtzFiredTriggers implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "定时任务名称")
    @TableId(value = "SCHED_NAME", type = IdType.AUTO)
    private String schedName;

    @Schema(name = "entryId")
    private String entryId;

    @Schema(name = "触发器名称")
    private String triggerName;

    @Schema(name = "触发器组")
    private String triggerGroup;

    @Schema(name = "实例名称")
    private String instanceName;

    @Schema(name = "执行时间")
    private Long firedTime;

    @Schema(name = "定时任务时间")
    private Long schedTime;

    @Schema(name = "等级")
    private Integer priority;

    @Schema(name = "状态")
    private String state;

    @Schema(name = "job名称")
    private String jobName;

    @Schema(name = "job组")
    private String jobGroup;

    @Schema(name = "是否异步")
    private String isNonconcurrent;

    @Schema(name = "是否请求覆盖")
    private String requestsRecovery;


}
