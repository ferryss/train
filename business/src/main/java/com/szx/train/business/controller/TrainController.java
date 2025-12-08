package com.szx.train.business.controller;

import com.szx.train.business.resp.TrainQueryResp;
import com.szx.train.business.service.TrainService;
import com.szx.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/train")
public class TrainController {

    @Resource
    private TrainService trainService;

    @GetMapping("/query-all")
    public CommonResp<List<TrainQueryResp>> queryAll() {
        List<TrainQueryResp> list = trainService.queryList();
        return new CommonResp<>(list);
    }

}
