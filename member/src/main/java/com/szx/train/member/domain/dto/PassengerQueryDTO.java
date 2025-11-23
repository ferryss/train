package com.szx.train.member.domain.dto;

import com.szx.train.common.req.PageReq;
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
public class PassengerQueryDTO extends PageReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;


    private Long memberId;


    private String name;


    private String idCard;


    private String type;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
