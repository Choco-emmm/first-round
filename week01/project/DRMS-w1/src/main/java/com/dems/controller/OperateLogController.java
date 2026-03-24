package com.dems.controller;

import com.dems.pojo.Result;
import com.dems.service.OperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/operateLog")
public class OperateLogController {
    @Autowired
    OperateLogService operateLogService;
    /**
     * 管理员获取操作日志
     * @return
     */
    @GetMapping("/admin")
    public Result getOperateLog(){
        return Result.success(operateLogService.getOperateLogList());
    }
}
