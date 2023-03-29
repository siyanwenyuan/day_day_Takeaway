package com.chen.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 进行一个全局的异常处理，使用的是spring中AOP的方法
 */
//定义一个通知，添加哪些controller需要拦截
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody//最终需要返回一个jason数据
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 异常处理方法
     * @param ex
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        //打印出异常信息
        log.error(ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg=split[2]+"已存在";
            return R.error(msg);
        }

        return R.error("未知错误");


    }

    /**
     * 自定义异常处理
     * @param ex
     * @return
     */

    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        //打印出异常信息
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }
}
