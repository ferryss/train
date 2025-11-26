package com.szx.train.business.controller.admin;

import com.szx.train.common.context.LoginMemberContext;
import com.szx.train.common.resp.CommonResp;
import com.szx.train.common.resp.PageResp;
import com.szx.train.business.req.TrainSeatQueryReq;
import com.szx.train.business.req.TrainSeatSaveReq;
import com.szx.train.business.resp.TrainSeatQueryResp;
import com.szx.train.business.service.TrainSeatService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/train-seat")
public class TrainSeatAdminController {

    @Resource
    private TrainSeatService trainSeatService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody TrainSeatSaveReq req) {
        trainSeatService.saveTrainSeat(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainSeatQueryResp>> queryList(@Valid TrainSeatQueryReq req) {
        PageResp<TrainSeatQueryResp> list = trainSeatService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainSeatService.delete(id);
        return new CommonResp<>();
    }

}
