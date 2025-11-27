package com.szx.train.business.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * <p>
 * 每日车站
 * </p>
 *
 * @author Ferry
 * @since 2025-11-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("daily_train_station")
@Schema(name="DailyTrainStation对象", description="每日车站")
public class DailyTrainStation implements Serializable {

    private static final long serialVersionUID = 1L;

    @SchemaProperty(name = "id")
    private Long id;

    @SchemaProperty(name = "日期")
    private LocalDate date;

    @SchemaProperty(name = "车次编号")
    private String trainCode;

    @SchemaProperty(name = "站序")
    @TableField("`index`")
    private Integer index;

    @SchemaProperty(name = "站名")
    private String name;

    @SchemaProperty(name = "站名拼音")
    private String namePinyin;

    @SchemaProperty(name = "进站时间")
    private LocalTime inTime;

    @SchemaProperty(name = "出站时间")
    private LocalTime outTime;

    @SchemaProperty(name = "停站时长")
    private LocalTime stopTime;

    @SchemaProperty(name = "里程（公里）|从上一站到本站的距离")
    private BigDecimal km;

    @SchemaProperty(name = "新增时间")
    private LocalDateTime createTime;

    @SchemaProperty(name = "修改时间")
    private LocalDateTime updateTime;


}
