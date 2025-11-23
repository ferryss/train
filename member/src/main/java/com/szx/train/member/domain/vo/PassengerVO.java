package com.szx.train.member.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
public class PassengerVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String idCard;

    private String type;



}
