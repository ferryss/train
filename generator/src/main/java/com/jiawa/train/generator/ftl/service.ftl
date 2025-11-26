package com.szx.train.${module}.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.szx.train.common.resp.PageResp;
import com.szx.train.common.util.SnowUtil;
import com.szx.train.${module}.domain.${Domain};
import com.szx.train.${module}.mapper.${Domain}Mapper;
import com.szx.train.${module}.req.${Domain}QueryReq;
import com.szx.train.${module}.req.${Domain}SaveReq;
import com.szx.train.${module}.resp.${Domain}QueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ${Domain}Service extends ServiceImpl<${Domain}Mapper, ${Domain}> {

    private static final Logger LOG = LoggerFactory.getLogger(${Domain}Service.class);


    public void save${Domain}(${Domain}SaveReq req) {
        LocalDateTime now = LocalDateTime.now();
        ${Domain} ${domain} = BeanUtil.copyProperties(req, ${Domain}.class);
        if (ObjectUtil.isNull(${domain}.getId())) {
            ${domain}.setId(SnowUtil.getSnowflakeNextId());
            ${domain}.setCreateTime(now);
            ${domain}.setUpdateTime(now);
            save(${domain});
        } else {
            ${domain}.setUpdateTime(now);
            updateById(${domain});
        }
    }

    public PageResp<${Domain}QueryResp> queryList(${Domain}QueryReq req) {
        IPage<${Domain}> page = new Page<>(req.getPage(), req.getSize());

        IPage<${Domain}> list = lambdaQuery()
            //.eq(LoginMemberContext.getId() != null , ${Domain}::getMemberId, LoginMemberContext.getId())
            .orderByDesc(${Domain}::getCreateTime)
            .page(page);

        if(list.getRecords().isEmpty()){
        return null;
        }

        List<${Domain}QueryResp> ${domain}QueryRespList = list.getRecords().stream().map(item -> {
            ${Domain}QueryResp ${domain}QueryResp = BeanUtil.copyProperties(item, ${Domain}QueryResp.class);
            return ${domain}QueryResp;
            }).toList();

        IPage<${Domain}QueryResp> ${domain}QueryRespPage = new Page<>(req.getPage(), req.getSize());
        ${domain}QueryRespPage.setTotal(list.getTotal());
        ${domain}QueryRespPage.setRecords(${domain}QueryRespList);


        PageResp<${Domain}QueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(${domain}QueryRespPage.getTotal());
        pageResp.setList(${domain}QueryRespPage.getRecords());
        return pageResp;
    }

    public void delete(Long id) {
        removeById(id);
    }

    public ${Domain}QueryResp queryById(Long id) {

                ${Domain} byId = getById(id);
        if(byId == null){
        return null;
        }

        return BeanUtil.copyProperties(byId, ${Domain}QueryResp.class);
    }
}
