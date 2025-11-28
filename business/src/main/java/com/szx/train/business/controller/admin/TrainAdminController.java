package com.szx.train.business.controller.admin;

import com.szx.train.business.req.TrainQueryReq;
import com.szx.train.business.req.TrainSaveReq;
import com.szx.train.business.resp.TrainQueryResp;
import com.szx.train.business.service.TrainService;
import com.szx.train.common.resp.CommonResp;
import com.szx.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/train")
public class TrainAdminController {

    @Resource
    private TrainService trainService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody TrainSaveReq req) {
        trainService.saveTrain(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainQueryResp>> queryList(@Valid TrainQueryReq req) {
        PageResp<TrainQueryResp> list = trainService.queryPageList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainService.delete(id);
        return new CommonResp<>();
    }

    @GetMapping("/query/{id}")
    public CommonResp<TrainQueryResp> query(@PathVariable Long id) {
        return new CommonResp<>(trainService.queryById(id));
    }

    @GetMapping("/query-all")
    public CommonResp<List<TrainQueryResp>> queryAll() {
        List<TrainQueryResp> list = trainService.queryList();
        return new CommonResp<>(list);
    }

}
