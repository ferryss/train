package com.szx.train.business.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.szx.train.common.req.PageReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmOrderQueryReq extends PageReq {

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    private String trainCode;
    @Override
    public String toString() {
        return "ConfirmOrderQueryReq{" +
                "} " + super.toString();
    }
}
