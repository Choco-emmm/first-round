package com.dems.exception;

import com.dems.pojo.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public Result handleException(Exception e, HttpServletRequest request){
        //记录异常日志
        log.error("【全局异常】拦截到未处理异常 | 请求URI: {} | 异常类: {}",
                request.getRequestURI(),
                e.getClass().getName(),
                e);
       return Result.error(e.getMessage());
    }
}
