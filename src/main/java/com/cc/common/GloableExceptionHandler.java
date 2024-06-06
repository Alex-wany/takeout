package com.cc.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 * 基于代理实现，也就是通过AOP来对这些异常进行拦截
 * 如果抛异常了，就一起来这里进行处理
 * @ControllerAdvice(拦截哪些controller)
 * @ControllerAdvice(annotations = {RestController.class, Controller.class})加入了@RestController、@Controller的注解
 * @ResponseBody 返回Result对象时解析成json
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GloableExceptionHandler {
    /**
     * @ExceptionHandler({异常类型.class,异常类型.class})
     * @param exception 异常对象，后面Get信息要用
     * @return 返回Result对象，既然都抓异常了，肯定就要Result.error("失败信息")
     */
    @ExceptionHandler({SQLIntegrityConstraintViolationException.class,NullPointerException.class})
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException exception){
        //打印异常信息
        log.error(exception.getMessage());
        //判断异常信息是否包含重复信息 Duplicate entry
        if (exception.getMessage().contains("Duplicate entry")){
            //exception对象分割，同时存储
            String []splitErrorMessage=exception.getMessage().split(" ");
            /**
             * splitErrorMessage数组内存的信息
             * Duplicate entry '新增的账号' for key 'idx_username'
             * 下标位2是新增账号，下标位5是关联的字段名
             */
            String errorMessage = "这个账号重复了" + splitErrorMessage[2];
            return Result.error(errorMessage);
        }
        //检查是否违反了其他常见的完整性约束
        else if (exception.getMessage().contains("Cannot delete or update a parent row")) {
            return Result.error("违反了外键约束");
        }
        else if (exception.getMessage().contains("Data too long for column")) {
            return Result.error("数据太长，超过了列的长度约束");
        }
        //返回未知错误
        return Result.error("未知错误");
    }
    /*@ExceptionHandler({SQLIntegrityConstraintViolationException.class,NullPointerException.class})
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException exception){
        //打印异常信息
        log.error(exception.getMessage());
        //判断异常信息是否包含重复信息 Duplicate entry
        if (exception.getMessage().contains("Duplicate entry")){
            //exception对象分割，同时存储
            String []splitErrorMessage=exception.getMessage().split(" ");
            *//**
             * splitErrorMessage数组内存的信息
             * Duplicate entry '新增的账号' for key 'idx_username'
             * 下标位2是新增账号，下标位5是关联的字段名
             *//*
            String errorMessage = "这个账号重复了" + splitErrorMessage[2];
            return Result.error(errorMessage);
        }
        //返回位置错误
        return Result.error("未知错误");
    }*/

    /**
     * 自定义的全局异常处理
     * @param customerException 自定义异常对象
     * @return
     */
    @ExceptionHandler({CustomerException.class})
    public Result<String> exceptionHandlerCustomer(CustomerException customerException){
        log.error(customerException.getMessage());
        //直接返回处理信息
        return Result.error(customerException.getMessage());
    }
}
