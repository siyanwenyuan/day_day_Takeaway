package com.chen.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;


//使用该注解，可以进行控制台的日志输出
@Slf4j
//创建引导类
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement//开启事务注解
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class,args);
        //使用了注解，可以再控制台输出info级别的日志
        log.info("控制台启动成功");
    }
}

