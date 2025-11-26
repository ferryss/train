package com.szx.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szx.train.business.domain.ConfirmOrder;
import com.szx.train.business.mapper.ConfirmOrderMapper;
import com.szx.train.business.req.ConfirmOrderQueryReq;
import com.szx.train.business.req.ConfirmOrderSaveReq;
import com.szx.train.business.resp.ConfirmOrderQueryResp;
import com.szx.train.common.context.LoginMemberContext;
import com.szx.train.common.resp.PageResp;
import com.szx.train.common.util.SnowUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConfirmOrderService extends ServiceImpl<ConfirmOrderMapper, ConfirmOrder> {

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);


    public void saveConfirmOrder(ConfirmOrderSaveReq req) {
        LocalDateTime now = LocalDateTime.now();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);
        if (ObjectUtil.isNull(confirmOrder.getId())) {
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            save(confirmOrder);
        } else {
            confirmOrder.setUpdateTime(now);
            updateById(confirmOrder);
        }
    }

    public PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req) {
        IPage<ConfirmOrder> page = new Page<>(req.getPage(), req.getSize());

        IPage<ConfirmOrder> list = lambdaQuery()
            .eq(LoginMemberContext.getId() != null , ConfirmOrder::getMemberId, LoginMemberContext.getId())
            .orderByDesc(ConfirmOrder::getCreateTime)
            .page(page);

        if(list.getRecords().isEmpty()){
        return null;
        }

        List<ConfirmOrderQueryResp> confirmOrderQueryRespList = list.getRecords().stream().map(item -> {
            ConfirmOrderQueryResp confirmOrderQueryResp = BeanUtil.copyProperties(item, ConfirmOrderQueryResp.class);
            return confirmOrderQueryResp;
            }).toList();

        IPage<ConfirmOrderQueryResp> confirmOrderQueryRespPage = new Page<>(req.getPage(), req.getSize());
        confirmOrderQueryRespPage.setTotal(list.getTotal());
        confirmOrderQueryRespPage.setRecords(confirmOrderQueryRespList);


        PageResp<ConfirmOrderQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(confirmOrderQueryRespPage.getTotal());
        pageResp.setList(confirmOrderQueryRespPage.getRecords());
        return pageResp;
    }

    public void delete(Long id) {
        removeById(id);
    }

    public ConfirmOrderQueryResp queryById(Long id) {

                ConfirmOrder byId = getById(id);
        if(byId == null){
        return null;
        }

        return BeanUtil.copyProperties(byId, ConfirmOrderQueryResp.class);
    }
}
