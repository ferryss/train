package com.szx.train.member.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@AllArgsConstructor
@NoArgsConstructor
public class PassengerDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;


    private Long memberId;

    @NotBlank(message = "【乘车人姓名】不能为空")
    private String name;

    @NotBlank(message = "【乘车人身份证号】不能为空")
    private String idCard;

    @NotBlank(message = "【乘车人类型】不能为空")
    private String type;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
