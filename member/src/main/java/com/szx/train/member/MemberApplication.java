package com.szx.train.member;

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
public class MemberApplication {
    public static void main(String[] args) {
        SpringApplication.run(MemberApplication.class, args);
    }
}
