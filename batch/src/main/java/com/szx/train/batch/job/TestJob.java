package com.szx.train.batch.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * @author ferry
 * @date 2025/12/1
 * @project train
 * @description
 */
public class TestJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        System.out.println("测试任务执行了");
    }
}
