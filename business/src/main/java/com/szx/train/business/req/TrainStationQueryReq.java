package com.szx.train.business.req;

import com.szx.train.common.req.PageReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TrainStationQueryReq extends PageReq {

    private String trainCode;

    private Integer index;

}
