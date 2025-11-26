package com.szx.train.business.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 车站
 * </p>
 *
 * @author Ferry
 * @since 2025-11-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("station")
@Schema(name="Station对象", description="车站")
public class Station implements Serializable {

    private static final long serialVersionUID = 1L;

    @SchemaProperty(name = "id")
    private Long id;

    @SchemaProperty(name = "站名")
    private String name;

    @SchemaProperty(name = "站名拼音")
    private String namePinyin;

    @SchemaProperty(name = "站名拼音首字母")
    private String namePy;

    @SchemaProperty(name = "新增时间")
    private LocalDateTime createTime;

    @SchemaProperty(name = "修改时间")
    private LocalDateTime updateTime;


}
