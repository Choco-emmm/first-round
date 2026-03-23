package com.dems.aop;

import com.dems.mapper.OperateLogMapper;
import com.dems.pojo.OperateLog;
import com.dems.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class OperationLogAspect {
    @Autowired
    private OperateLogMapper operateLogMapper;

    //加了@LogOperation的类方法，会执行此方法（给增删改方法加了，注册除外，因为注册没有操作人信息）
    @Around("@annotation(com.dems.anno.LogOperation)")
    public Object recordLog(ProceedingJoinPoint joinPoint)  {
        long startTime = System.currentTimeMillis();
        OperateLog operateLog = new OperateLog();
        //操作时间
        operateLog.setOperateTime(LocalDateTime.now());
        //当前用户id
        operateLog.setOperateUserId(UserContext.getUserId());
        //方法所属类名
        operateLog.setClassName(joinPoint.getTarget().getClass().getName());
        //方法名
        operateLog.setMethodName(joinPoint.getSignature().getName());
        //方法参数
        operateLog.setMethodParams(Arrays.toString(joinPoint.getArgs()));
        Object result=null;
        //执行方法
        try {
            result = joinPoint.proceed();
        }catch (Throwable e){
            operateLog.setExceptionMsg(e.getMessage());
        }finally {
            //方法返回值的类型
            operateLog.setReturnValue(result != null ? result.getClass().getName() : "void");
            //计算耗时
            long endTime = System.currentTimeMillis();
            operateLog.setCostTime(endTime-startTime);
            //保存日志
            log.info("操作日志：{}", operateLog);
            operateLogMapper.insertLog(operateLog);
            //抛出异常给全局处理器，返回错误信息给前端
            if(operateLog.getExceptionMsg() != null){
                throw new RuntimeException(operateLog.getExceptionMsg());
            }
        }
        return result;//给controller返回结果
    }
}
