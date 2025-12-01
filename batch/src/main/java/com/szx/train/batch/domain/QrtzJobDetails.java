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
@TableName("QRTZ_JOB_DETAILS")
@Schema(name="QrtzJobDetails对象", description="")
public class QrtzJobDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "定时任务名称")
    @TableId(value = "SCHED_NAME", type = IdType.AUTO)
    private String schedName;

    @Schema(name = "job名称")
    private String jobName;

    @Schema(name = "job组")
    private String jobGroup;

    @Schema(name = "描述")
    private String description;

    @Schema(name = "job类名")
    private String jobClassName;

    @Schema(name = "是否持久化")
    private String isDurable;

    @Schema(name = "是否非同步")
    private String isNonconcurrent;

    @Schema(name = "是否更新数据")
    private String isUpdateData;

    @Schema(name = "请求是否覆盖")
    private String requestsRecovery;

    @Schema(name = "job数据")
    private Blob jobData;


}
