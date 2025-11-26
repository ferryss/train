package com.szx.train.business.controller.admin;

import com.szx.train.business.req.DailyTrainQueryReq;
import com.szx.train.business.req.DailyTrainSaveReq;
import com.szx.train.business.resp.DailyTrainQueryResp;
import com.szx.train.business.service.DailyTrainService;
import com.szx.train.common.resp.CommonResp;
import com.szx.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/daily-train")
public class DailyTrainAdminController {

    @Resource
    private DailyTrainService dailyTrainService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainSaveReq req) {
        dailyTrainService.saveDailyTrain(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainQueryResp>> queryList(@Valid DailyTrainQueryReq req) {
        PageResp<DailyTrainQueryResp> list = dailyTrainService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainService.delete(id);
        return new CommonResp<>();
    }

    @GetMapping("/query/{id}")
    public CommonResp<DailyTrainQueryResp> query(@PathVariable Long id) {
        return new CommonResp<>(dailyTrainService.queryById(id));
    }

}
