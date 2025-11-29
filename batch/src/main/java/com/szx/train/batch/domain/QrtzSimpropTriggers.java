package com.szx.train.batch.domain;

import java.math.BigDecimal;
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
@TableName("QRTZ_SIMPROP_TRIGGERS")
@ApiModel(value="QrtzSimpropTriggers对象", description="")
public class QrtzSimpropTriggers implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "定时任务名称")
    @TableId(value = "SCHED_NAME", type = IdType.AUTO)
    private String schedName;

    @ApiModelProperty(value = "触发器名称")
    private String triggerName;

    @ApiModelProperty(value = "触发器组")
    private String triggerGroup;

    @ApiModelProperty(value = "开始配置1")
    private String strProp1;

    @ApiModelProperty(value = "开始配置2")
    private String strProp2;

    @ApiModelProperty(value = "开始配置3")
    private String strProp3;

    @ApiModelProperty(value = "int配置1")
    private Integer intProp1;

    @ApiModelProperty(value = "int配置2")
    private Integer intProp2;

    @ApiModelProperty(value = "long配置1")
    private Long longProp1;

    @ApiModelProperty(value = "long配置2")
    private Long longProp2;

    @ApiModelProperty(value = "配置描述1")
    private BigDecimal decProp1;

    @ApiModelProperty(value = "配置描述2")
    private BigDecimal decProp2;

    @ApiModelProperty(value = "bool配置1")
    private String boolProp1;

    @ApiModelProperty(value = "bool配置2")
    private String boolProp2;


}
