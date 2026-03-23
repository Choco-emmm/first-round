package com.dems.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperateLog {
    private Integer id;                // 主键 ID
    private String operateUserId;   // 操作用户ID
    private LocalDateTime operateTime; // 操作时间
    private String className;       // 被调用的类名
    private String methodName;      // 被调用的方法名
    private String methodParams;    // 方法参数
    private String returnValue;     // 返回值
    private Long costTime;          // 耗时（毫秒）
    private String exceptionMsg;       // 异常信息
}
