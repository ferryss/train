package com.szx.train.business.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TrainGenerateReq {

    @NotBlank
    private String code;

    @NotBlank
    private String type;

}
