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
@TableName("QRTZ_PAUSED_TRIGGER_GRPS")
@Schema(name="QrtzPausedTriggerGrps对象", description="")
public class QrtzPausedTriggerGrps implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "定时任务名称")
    @TableId(value = "SCHED_NAME", type = IdType.AUTO)
    private String schedName;

    @Schema(name = "触发器组")
    private String triggerGroup;


}
