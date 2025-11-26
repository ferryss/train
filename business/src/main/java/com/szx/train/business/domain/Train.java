package com.szx.train.business.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * <p>
 * 车次
 * </p>
 *
 * @author Ferry
 * @since 2025-11-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("train")
@Schema(name="Train对象", description="车次")
public class Train implements Serializable {

    private static final long serialVersionUID = 1L;

    @SchemaProperty(name = "id")
    private Long id;

    @SchemaProperty(name = "车次编号")
    private String code;

    @SchemaProperty(name = "车次类型|枚举[TrainTypeEnum]")
    private String type;

    @SchemaProperty(name = "始发站")
    private String start;

    @SchemaProperty(name = "始发站拼音")
    private String startPinyin;

    @SchemaProperty(name = "出发时间")
    private LocalTime startTime;

    @SchemaProperty(name = "终点站")
    private String end;

    @SchemaProperty(name = "终点站拼音")
    private String endPinyin;

    @SchemaProperty(name = "到站时间")
    private LocalTime endTime;

    @SchemaProperty(name = "新增时间")
    private LocalDateTime createTime;

    @SchemaProperty(name = "修改时间")
    private LocalDateTime updateTime;


}
