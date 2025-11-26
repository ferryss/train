package com.szx.train.business.controller.admin;

import com.szx.train.common.context.LoginMemberContext;
import com.szx.train.common.resp.CommonResp;
import com.szx.train.common.resp.PageResp;
import com.szx.train.business.req.TrainCarriageQueryReq;
import com.szx.train.business.req.TrainCarriageSaveReq;
import com.szx.train.business.resp.TrainCarriageQueryResp;
import com.szx.train.business.service.TrainCarriageService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/train-carriage")
public class TrainCarriageAdminController {

    @Resource
    private TrainCarriageService trainCarriageService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody TrainCarriageSaveReq req) {
        trainCarriageService.saveTrainCarriage(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainCarriageQueryResp>> queryList(@Valid TrainCarriageQueryReq req) {
        PageResp<TrainCarriageQueryResp> list = trainCarriageService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainCarriageService.delete(id);
        return new CommonResp<>();
    }

}
