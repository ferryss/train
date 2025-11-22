package com.szx.train.member.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 会员
 * </p>
 *
 * @author Ferry
 * @since 2025-11-19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "【会员电话】不能为空")
    private String mobile;

    @NotBlank(message = "【会员验证码】不能为空")
    private String code;


}
