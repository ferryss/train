package com.szx.train.common.controller;


import com.szx.train.common.exception.BusinessException;
import com.szx.train.common.resp.CommonResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常处理、数据预处理等
 */
@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {


    /**
     * 所有异常统一处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public CommonResp exceptionHandler(Exception e) {
        CommonResp commonResp = new CommonResp();
        log.error("系统异常：", e);
        commonResp.setSuccess(false);
        commonResp.setMessage("系统出现异常，请联系管理员");
        return commonResp;
    }

    /**
     * 业务异常统一处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public CommonResp exceptionHandler(BusinessException e) {
        CommonResp commonResp = new CommonResp();
        log.error("业务异常：{}", e.getE().getDesc());
        commonResp.setSuccess(false);
        commonResp.setMessage(e.getE().getDesc());
        return commonResp;
    }

    /**
     * 校验异常统一处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public CommonResp exceptionHandler(BindException e) {
        CommonResp commonResp = new CommonResp();

        for (ObjectError error : e.getBindingResult().getAllErrors()) {
            log.error("校验异常：{}", error.getDefaultMessage());
        }
        commonResp.setSuccess(false);
        commonResp.setMessage(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return commonResp;
    }


    /**
     * 捕获SQL异常
     * @param ex
     * @return
     */
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseBody
    public CommonResp exceptionHandler(DuplicateKeyException ex){
        CommonResp commonResp = new CommonResp();
        commonResp.setSuccess(false);
        //获取异常信息
        log.error("异常信息：{}", ex.getMessage());
        String message = ex.getMessage();
        //Duplicate entry 'lixi' for key 'employee.idx_username'
        if(message.contains("Duplicate entry")){
            String[] split = message.split("Duplicate entry ");
            String[] split1 = split[1].split("for key");
            String info = split1[0];
            String msg = info + "已经存在";
            commonResp.setMessage(msg);
            return commonResp;
        }

        return commonResp;
    }

}
