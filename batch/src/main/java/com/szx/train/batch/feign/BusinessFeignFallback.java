package com.szx.train.batch.feign;

import com.szx.train.common.resp.CommonResp;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author ferry
 * @date 2026/1/21
 * @project train
 * @description
 */

@Component
public class BusinessFeignFallback implements BusinessFeign{
    @Override
    public CommonResp<Object> genDaily(Date date) {
        return null;
    }
}
