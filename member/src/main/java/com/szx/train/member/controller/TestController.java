package com.szx.train.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ferry
 * @date 2025/11/21
 * @project train
 * @description
 */
@RestController
@RequestMapping
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/test")
    public void test(HttpServletRequest  request){
        String header = request.getHeader("user-info");
        log.info("==========访问成功==========");
        log.info("用户信息：{}", header);
    }
}
