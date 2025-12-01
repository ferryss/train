package com.szx.train.batch.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

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
@Schema(name="QrtzSimpropTriggers对象", description="")
public class QrtzSimpropTriggers implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "定时任务名称")
    @TableId(value = "SCHED_NAME", type = IdType.AUTO)
    private String schedName;

    @Schema(name = "触发器名称")
    private String triggerName;

    @Schema(name = "触发器组")
    private String triggerGroup;

    @Schema(name = "开始配置1")
    private String strProp1;

    @Schema(name = "开始配置2")
    private String strProp2;

    @Schema(name = "开始配置3")
    private String strProp3;

    @Schema(name = "int配置1")
    private Integer intProp1;

    @Schema(name = "int配置2")
    private Integer intProp2;

    @Schema(name = "long配置1")
    private Long longProp1;

    @Schema(name = "long配置2")
    private Long longProp2;

    @Schema(name = "配置描述1")
    private BigDecimal decProp1;

    @Schema(name = "配置描述2")
    private BigDecimal decProp2;

    @Schema(name = "bool配置1")
    private String boolProp1;

    @Schema(name = "bool配置2")
    private String boolProp2;


}
