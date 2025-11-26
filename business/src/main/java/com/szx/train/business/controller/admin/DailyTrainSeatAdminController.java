package com.szx.train.business.controller.admin;

import com.szx.train.business.req.DailyTrainSeatQueryReq;
import com.szx.train.business.req.DailyTrainSeatSaveReq;
import com.szx.train.business.resp.DailyTrainSeatQueryResp;
import com.szx.train.business.service.DailyTrainSeatService;
import com.szx.train.common.resp.CommonResp;
import com.szx.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/daily-train-seat")
public class DailyTrainSeatAdminController {

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainSeatSaveReq req) {
        dailyTrainSeatService.saveDailyTrainSeat(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainSeatQueryResp>> queryList(@Valid DailyTrainSeatQueryReq req) {
        PageResp<DailyTrainSeatQueryResp> list = dailyTrainSeatService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainSeatService.delete(id);
        return new CommonResp<>();
    }

    @GetMapping("/query/{id}")
    public CommonResp<DailyTrainSeatQueryResp> query(@PathVariable Long id) {
        return new CommonResp<>(dailyTrainSeatService.queryById(id));
    }

}
