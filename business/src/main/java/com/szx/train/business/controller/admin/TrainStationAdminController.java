package com.szx.train.business.controller.admin;

import com.szx.train.business.req.TrainStationQueryReq;
import com.szx.train.business.req.TrainStationSaveReq;
import com.szx.train.business.resp.TrainStationQueryResp;
import com.szx.train.business.service.TrainStationService;
import com.szx.train.common.resp.CommonResp;
import com.szx.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/train-station")
public class TrainStationAdminController {

    @Resource
    private TrainStationService trainStationService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody TrainStationSaveReq req) {
        trainStationService.saveTrainStation(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainStationQueryResp>> queryList(@Valid TrainStationQueryReq req) {
        PageResp<TrainStationQueryResp> list = trainStationService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainStationService.delete(id);
        return new CommonResp<>();
    }

    @GetMapping("/query/{id}")
    public CommonResp<TrainStationQueryResp> query(@PathVariable Long id) {
        return new CommonResp<>(trainStationService.queryById(id));
    }

    @GetMapping("/query")
    public CommonResp<TrainStationQueryResp> queryByTrainCodeAndIndex(@RequestParam String trainCode,
                                                   @RequestParam Integer index) {
        return new CommonResp<>(trainStationService.queryByTrainCodeAndIndex(trainCode, index));
    }

}
