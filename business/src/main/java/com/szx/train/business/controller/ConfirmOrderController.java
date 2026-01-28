package com.szx.train.business.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.szx.train.business.req.ConfirmOrderDoReq;
import com.szx.train.business.service.BeforeConfirmOrderService;
import com.szx.train.business.service.ConfirmOrderService;
import com.szx.train.common.exception.BusinessExceptionEnum;
import com.szx.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/confirm-order")
public class ConfirmOrderController {

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

    @Resource
    private BeforeConfirmOrderService beforeconfirmOrderService;

    @SentinelResource(value = "confirmOrderDo", blockHandler = "doConfirmOrderBlock")
    @PostMapping("/do")
    public CommonResp<Object> save(@Valid @RequestBody ConfirmOrderDoReq req) {
        beforeconfirmOrderService.beforeDoConfirmOrder(req);
        return new CommonResp<>();
    }

    /**
     * 降级方法
     * @param req
     * @param e
     */
    public CommonResp<Object> doConfirmOrderBlock(ConfirmOrderDoReq  req, BlockException e){
        LOG.info("购票请求被限流 {}", req);
        CommonResp<Object> CommonResp = new CommonResp<>();
        CommonResp.setSuccess(false);
        CommonResp.setMessage(BusinessExceptionEnum.CONFIRM_ORDER_FLOE_ERROR.getDesc());
        return CommonResp;
    }

}
