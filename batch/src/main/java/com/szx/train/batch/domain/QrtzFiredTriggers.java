package com.szx.train.batch.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
@TableName("QRTZ_FIRED_TRIGGERS")
@ApiModel(value="QrtzFiredTriggers对象", description="")
public class QrtzFiredTriggers implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "定时任务名称")
    @TableId(value = "SCHED_NAME", type = IdType.AUTO)
    private String schedName;

    @ApiModelProperty(value = "entryId")
    private String entryId;

    @ApiModelProperty(value = "触发器名称")
    private String triggerName;

    @ApiModelProperty(value = "触发器组")
    private String triggerGroup;

    @ApiModelProperty(value = "实例名称")
    private String instanceName;

    @ApiModelProperty(value = "执行时间")
    private Long firedTime;

    @ApiModelProperty(value = "定时任务时间")
    private Long schedTime;

    @ApiModelProperty(value = "等级")
    private Integer priority;

    @ApiModelProperty(value = "状态")
    private String state;

    @ApiModelProperty(value = "job名称")
    private String jobName;

    @ApiModelProperty(value = "job组")
    private String jobGroup;

    @ApiModelProperty(value = "是否异步")
    private String isNonconcurrent;

    @ApiModelProperty(value = "是否请求覆盖")
    private String requestsRecovery;


}
