package com.szx.train.batch.job;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.szx.train.batch.feign.BusinessFeign;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author ferry
 * @date 2025/12/1
 * @project train
 * @description
 */
@DisallowConcurrentExecution //不允许并发执行
@Slf4j
public class DailyTrainJob implements Job {

    @Autowired
    private BusinessFeign businessFeign;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        // 增加日志流水号
        MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
        log.info("正在生成每日车次数据");
        DateTime now = DateTime.now();
        DateTime offset = DateUtil.offset(now, DateField.DAY_OF_YEAR, 14);
        businessFeign.genDaily(offset.toJdkDate());
        log.info("✅生成每日车次数据完成");
    }
}
