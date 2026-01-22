package com.szx.train.business.controller.admin;


import com.szx.train.business.domain.SkToken;
import com.szx.train.business.req.SkTokenQueryReq;
import com.szx.train.business.req.SkTokenReq;
import com.szx.train.business.service.SkTokenService;
import com.szx.train.common.resp.CommonResp;
import com.szx.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 秒杀令牌 前端控制器
 * </p>
 *
 * @author Ferry
 * @since 2026-01-22
 */
@RestController
@RequestMapping("/admin/sk-token")
public class SkTokenController {
    @Resource
    private SkTokenService skTokenService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody SkTokenReq req) {
        skTokenService.saveSkToken(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<SkToken>> queryList(@Valid SkTokenQueryReq req) {
        PageResp<SkToken> list = skTokenService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        skTokenService.delete(id);
        return new CommonResp<>();
    }

    @GetMapping("/query/{id}")
    public CommonResp<SkToken> query(@PathVariable Long id) {
        return new CommonResp<>(skTokenService.queryById(id));
    }
}
