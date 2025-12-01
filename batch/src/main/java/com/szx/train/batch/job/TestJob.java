package com.szx.train.batch.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * @author ferry
 * @date 2025/12/1
 * @project train
 * @description
 */
@DisallowConcurrentExecution //不允许并发执行
public class TestJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        System.out.println("测试任务执行了");
    }
}
