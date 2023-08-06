package com.eastwind.common;

/*
@author zhangJH
@create 2023-07-23-20:44
*/


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

// @RestControllerAdvice由ControllerAdvice和ResponseBody组合
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // @ExceptionHandler(被处理的异常类)
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        // ex.getMessage()得到具体错误信息
        log.error(ex.getMessage());
        // 如果错误信息包含Duplicate entry，说明用户名已存在，提示错误
        if (ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return Result.error(msg);
        }
        return Result.error("未知异常");
    }

    @ExceptionHandler(CustomException.class)
    public Result<String> exceptionHandler(CustomException ex){
        log.info(ex.getMessage());  // 得到错误信息
        return Result.error(ex.getMessage());
    }
}
