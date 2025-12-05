package com.szx.train.batch.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

//@FeignClient(name = "business")
@FeignClient(name = "business", url = "http://localhost:8082")
public interface BusinessFeign {

    @GetMapping("/business/daily/gen")
    void genDaily();
}
