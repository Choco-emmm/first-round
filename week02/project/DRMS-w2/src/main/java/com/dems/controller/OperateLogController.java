package com.dems.controller;

import com.dems.pojo.Result;
import com.dems.service.OperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/operateLog")
public class OperateLogController {
    @Autowired
    private OperateLogService operateLogService;
    /**
     * 管理员获取操作日志
     * @return
     */
    @GetMapping("/admin")
    public Result getOperateLog(){
        return Result.success(operateLogService.getOperateLogList());
    }
}
