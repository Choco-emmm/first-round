package com.dems.mapper;

import com.dems.pojo.OperateLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperateLogMapper {
    //插入操作日志
    void insertLog(OperateLog operateLog);
}
