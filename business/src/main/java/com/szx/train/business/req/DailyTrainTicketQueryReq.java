package com.szx.train.business.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.szx.train.common.req.PageReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DailyTrainTicketQueryReq extends PageReq {

    String trainCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    String date;

    String start;

    String end;

}
