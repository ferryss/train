package com.szx.train.business.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @author ferry
 * @date 2026/1/28
 * @project train
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ConfirmOrderMQDto {

    private String logId;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date date;

    private String trainCode;
}
