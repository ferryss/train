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
import com.szx.train.common.context.LoginMemberContext;
import com.szx.train.common.resp.PageResp;
import com.szx.train.common.util.SnowUtil;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.redisson.api.RedissonClient;
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
    private final RedissonClient redissonClient;
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
        LOG.info("查询日期【{}】车次【{}】的令牌余量", DateUtil.formatDate(date), trainCode);
        Long memberId = LoginMemberContext.getId();

//        // 先获取令牌防止机器人刷票
//        String lockKey = DateUtil.formatDate(date) + "-" + trainCode + "-" + memberId;
//        RLock lock = redissonClient.getLock(lockKey);
//        try {
//            boolean b = lock.tryLock(5, TimeUnit.SECONDS);
//            if(Boolean.TRUE.equals(b)){
//                LOG.info("获取令牌锁成功, lockKey: {}", lockKey);
//            }else{
//                LOG.info("获取令牌锁失败, lockKey: {}", lockKey);
//                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_LOCK_FAIL);
//            }
//        } catch (InterruptedException e) {
//            LOG.error("获取令牌锁失败");
//        }

        // 使用Simple执行器获取准确影响行数
        try (SqlSession session = sqlSessionFactory.openSession(ExecutorType.SIMPLE)) {
            SkTokenMapper mapper = session.getMapper(SkTokenMapper.class);
            int updateCount = mapper.decrease(date, trainCode);
            session.commit();
            LOG.info("影响行数{}", updateCount);
            return updateCount > 0;
        }

        /*
         改动
         1.获取Redis中的令牌余量
         2.如果缓存存在
            (1)去更新Redis中的令牌余量，就是减1
            (2)如果令牌余量小于0，返回false
            (3)否则，增加过期时间，并且count % 5 = 0时，再去更新数据库，返回true
         3.如果缓存不存在
            (1)从数据库中查询令牌余量
            (2)对其余量做判断，是否存在该令牌，数量是否大于0
            (3)对结果做减1，增加缓存和更新数据库
         */



    }
}
