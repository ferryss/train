package com.szx.train.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ferry
 * @date 2025/11/18
 * @project train
 * @description
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Operation(summary = "测试")
    @GetMapping
    public void test(){
        System.out.println("test sdf");
    }
}
