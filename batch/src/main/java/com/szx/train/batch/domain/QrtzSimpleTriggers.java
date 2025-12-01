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
@TableName("QRTZ_SIMPLE_TRIGGERS")
@Schema(name="QrtzSimpleTriggers对象", description="")
public class QrtzSimpleTriggers implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "定时任务名称")
    @TableId(value = "SCHED_NAME", type = IdType.AUTO)
    private String schedName;

    @Schema(name = "触发器名称")
    private String triggerName;

    @Schema(name = "触发器组")
    private String triggerGroup;

    @Schema(name = "重复执行次数")
    private Long repeatCount;

    @Schema(name = "重复执行间隔")
    private Long repeatInterval;

    @Schema(name = "已经触发次数")
    private Long timesTriggered;


}
