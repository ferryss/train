package com.szx.train.business.req;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 * 秒杀令牌
 * </p>
 *
 * @author Ferry
 * @since 2026-01-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sk_token")
@Schema(name="SkToken对象", description="秒杀令牌")
public class SkTokenReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @SchemaProperty(name = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @SchemaProperty(name = "日期")
    private LocalDate date;

    @SchemaProperty(name = "车次编号")
    private String trainCode;

    @SchemaProperty(name = "令牌余量")
    private Integer count;

}
