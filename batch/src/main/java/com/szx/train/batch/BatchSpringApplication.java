package com.szx.train.batch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author ferry
 * @date 2025/11/29
 * @project train
 * @description
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.szx.train"})
@MapperScan(basePackages = {"com.szx.train.batch.mapper"})
public class BatchSpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(BatchSpringApplication.class, args);
    }
}
