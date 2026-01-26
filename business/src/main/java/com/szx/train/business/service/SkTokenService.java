package com.szx.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szx.train.business.domain.DailyTrainSeat;
import com.szx.train.business.domain.SkToken;
import com.szx.train.business.mapper.SkTokenMapper;
import com.szx.train.business.req.SkTokenQueryReq;
import com.szx.train.business.req.SkTokenReq;
import com.szx.train.common.resp.PageResp;
import com.szx.train.common.util.SnowUtil;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 秒杀令牌 服务实现类
 * </p>
 *
 * @author Ferry
 * @since 2026-01-22
 */
@Service
@RequiredArgsConstructor
public class SkTokenService extends ServiceImpl<SkTokenMapper, SkToken> {

    private static final Logger LOG = LoggerFactory.getLogger(TrainSeatService.class);

    private final DailyTrainSeatService dailyTrainSeatService;
    private final SkTokenMapper skTokenMapper;
    private final SqlSessionFactory sqlSessionFactory;
    public void genDaily(Date date, String trainCode, Long trainStationCount) {
        LOG.info("删除日期 【{}】车次【{}】的令牌记录", DateUtil.formatDate(date), trainCode);
        lambdaUpdate()
                .eq(SkToken::getDate, date)
                .eq(SkToken::getTrainCode, trainCode)
                .remove();

        LocalDateTime now = LocalDateTime.now();
        SkToken skToken = new SkToken();
        skToken.setDate(date);
        skToken.setTrainCode(trainCode);
        skToken.setId(SnowUtil.getSnowflakeNextId());
        skToken.setCreateTime(now);
        skToken.setUpdateTime(now);

        int seatCount = dailyTrainSeatService.lambdaQuery()
                .eq(DailyTrainSeat::getDate, date)
                .eq(DailyTrainSeat::getTrainCode, trainCode)
                .count().intValue();
        LOG.info("车次【{}】的座位数【{}】", trainCode, seatCount);

        LOG.info("车次【{}】到站数【{}】", trainCode, trainStationCount);

        int count = (int) (seatCount * trainStationCount * 3/4);
        LOG.info("车次【{}】初始生成令牌数【{}】", trainCode, count);
        skToken.setCount(count);

        save(skToken);
    }

    public void saveSkToken(SkTokenReq req) {
        LocalDateTime now = LocalDateTime.now();
        SkToken skToken = BeanUtil.copyProperties(req, SkToken.class);
        if (ObjectUtil.isNull(skToken.getId())) {
            skToken.setId(SnowUtil.getSnowflakeNextId());
            skToken.setCreateTime(now);
            skToken.setUpdateTime(now);
            save(skToken);
        } else {
            skToken.setUpdateTime(now);
            updateById(skToken);
        }
    }

    public PageResp<SkToken> queryList(SkTokenQueryReq req) {
        IPage<SkToken> page = new Page<>(req.getPage(), req.getSize());

        IPage<SkToken> list = lambdaQuery()
                .eq(req.getDate() != null, SkToken::getDate, req.getDate())
                .eq(StrUtil.isNotBlank(req.getTrainCode()), SkToken::getTrainCode, req.getTrainCode())
                .orderByAsc(SkToken::getDate, SkToken::getTrainCode)
                .page(page);

        if(list.getRecords().isEmpty()){
            return null;
        }

        List<SkToken> skTokenList = list.getRecords();

        IPage<SkToken> skTokenPage = new Page<>(req.getPage(), req.getSize());
        skTokenPage.setTotal(list.getTotal());
        skTokenPage.setRecords(skTokenList);


        PageResp<SkToken> pageResp = new PageResp<>();
        pageResp.setTotal(skTokenPage.getTotal());
        pageResp.setList(skTokenPage.getRecords());

        return pageResp;
    }

    public void delete(Long id) {
        removeById(id);
    }

    public SkToken queryById(Long id) {

        SkToken byId = getById(id);
        if(byId == null){
            return null;
        }

        return BeanUtil.copyProperties(byId, SkToken.class);
    }


    public boolean validSkToken(Date date, String trainCode) {
        // 使用Simple执行器获取准确影响行数
        try (SqlSession session = sqlSessionFactory.openSession(ExecutorType.SIMPLE)) {
            SkTokenMapper mapper = session.getMapper(SkTokenMapper.class);
            int updateCount = mapper.decrease(date, trainCode);
            session.commit();
            LOG.info("影响行数{}", updateCount);
            return updateCount > 0;
        }
    }
}
