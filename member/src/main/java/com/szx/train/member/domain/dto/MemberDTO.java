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

    @NotBlank
    private String mobile;


}
