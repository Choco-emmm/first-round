package com.dems.service.impl;

import com.dems.mapper.OperateLogMapper;
import com.dems.pojo.OperateLog;
import com.dems.service.OperateLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OperateLogServiceImpl implements OperateLogService {
    @Autowired
    private OperateLogMapper OperateLogMapper;
    @Override
    public List<OperateLog> getOperateLogList() {
        log.info("开始查询操作日志列表");
        List<OperateLog> operateLogs = OperateLogMapper.selectAll();
        log.info("查询操作日志列表完成");
        return operateLogs;
    }
}
