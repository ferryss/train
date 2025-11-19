package com.szx.train.member.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 乘车人
 * </p>
 *
 * @author Ferry
 * @since 2025-11-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("passenger")
@Schema(name="Passenger对象", description="乘车人")
public class Passenger implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "会员id")
    private Long memberId;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "身份证")
    private String idCard;

    @Schema(description = "旅客类型|枚举[PassengerTypeEnum]")
    private String type;

    @Schema(description = "新增时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;


}
