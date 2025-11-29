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
@TableName("QRTZ_CALENDARS")
@ApiModel(value="QrtzCalendars对象", description="")
public class QrtzCalendars implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "定时任务名称")
    @TableId(value = "SCHED_NAME", type = IdType.AUTO)
    private String schedName;

    @ApiModelProperty(value = "日程名称")
    private String calendarName;

    @ApiModelProperty(value = "日程数据")
    private Blob calendar;


}
