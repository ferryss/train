package com.szx.train.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author ferry
 * @date 2025/11/18
 * @project train
 * @description
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.szx.train"})
@MapperScan(basePackages = {"com.szx.train.member.mapper"})
public class MemberApplication {
    public static void main(String[] args) {
        SpringApplication.run(MemberApplication.class, args);
    }
}
