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
@TableName("QRTZ_JOB_DETAILS")
@ApiModel(value="QrtzJobDetails对象", description="")
public class QrtzJobDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "定时任务名称")
    @TableId(value = "SCHED_NAME", type = IdType.AUTO)
    private String schedName;

    @ApiModelProperty(value = "job名称")
    private String jobName;

    @ApiModelProperty(value = "job组")
    private String jobGroup;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "job类名")
    private String jobClassName;

    @ApiModelProperty(value = "是否持久化")
    private String isDurable;

    @ApiModelProperty(value = "是否非同步")
    private String isNonconcurrent;

    @ApiModelProperty(value = "是否更新数据")
    private String isUpdateData;

    @ApiModelProperty(value = "请求是否覆盖")
    private String requestsRecovery;

    @ApiModelProperty(value = "job数据")
    private Blob jobData;


}
