package com.szx.train.batch.job;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.MDC;

/**
 * @author ferry
 * @date 2025/12/1
 * @project train
 * @description
 */
@DisallowConcurrentExecution //不允许并发执行
@Slf4j
public class DailyTrainJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        // 增加日志流水号
        MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
        log.info("正在生成每日车次数据");
        log.info("✅生成每日车次数据完成");
    }
}
